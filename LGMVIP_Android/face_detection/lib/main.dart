import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:firebase_ml_vision/firebase_ml_vision.dart';
import 'package:image_picker/image_picker.dart';
import 'dart:io';
import 'dart:ui' as ui;

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData.dark(),
      title: 'Face_Detection',
      home: Face_Home(),
    );
  }
}

class Face_Home extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Face Detection'),
        centerTitle: true,
        actions: <Widget>[
          IconButton(
              icon: Icon(Icons.account_circle),
              tooltip: 'About',
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute<void>(builder: (BuildContext context) {
                    return Scaffold(
                      backgroundColor: Colors.teal,
                      appBar: AppBar(
                        leading: IconButton(
                          icon: Icon(Icons.arrow_back_ios_sharp),
                          onPressed: () {
                            Navigator.pop(context);
                          },
                        ),
                        title: Text('About'),
                        centerTitle: true,
                      ),
                      body: SafeArea(
                        child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: <Widget>[
                              CircleAvatar(
                                radius: 50.0,
                                backgroundImage: AssetImage('files/j.jpeg'),
                              ),
                              Text(
                                'Jay Prakash Pandey',
                                style: TextStyle(
                                  //fontFamily: 'Pacifico',
                                  fontSize: 40.0,
                                  color: Colors.white,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                              Text(
                                'Engineer',
                                style: TextStyle(
                                  //fontFamily: 'Source Sans Pro',
                                  color: Colors.white,
                                  fontSize: 20.0,
                                  letterSpacing: 2.5,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                              SizedBox(
                                height: 20.0,
                                width: 150.0,
                                child: Divider(
                                  color: Colors.white,
                                ),
                              ),
                              Card(
                                  margin: EdgeInsets.symmetric(
                                      vertical: 10.0, horizontal: 25.0),
                                  child: ListTile(
                                    leading: Icon(
                                      Icons.phone,
                                      color: Colors.teal,
                                    ),
                                    onTap: () {},
                                    title: Text(
                                      '+91 6205269982',
                                      style: TextStyle(
                                        color: Colors.white,
                                        fontSize: 20.0,
                                      ),
                                    ),
                                  )),
                              Card(
                                  margin: EdgeInsets.symmetric(
                                      vertical: 10.0, horizontal: 25.0),
                                  child: ListTile(
                                      leading: Icon(
                                        Icons.email,
                                        color: Colors.teal,
                                      ),
                                      onTap: () {},
                                      title: Text(
                                        'jayprakashpandey47b@gmail.com',
                                        style: TextStyle(
                                          fontSize: 16.0,
                                          color: Colors.white,
                                        ),
                                      )))
                            ]),
                      ),
                    );
                  }),
                );
              }),
        ],
      ),
      body: Imagescn(),
      //TODO: make this important
    );
  }
}

class Imagescn extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<Imagescn> {
  File _imageFile;
  List<Face> _faces;
  bool isLoading = false;
  ui.Image _image;
  final picker = ImagePicker();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        floatingActionButton: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            FloatingActionButton(
              onPressed: _getFromCamera,
              tooltip: 'Open Camera',
              child: Icon(Icons.camera),
            ),
            SizedBox(
              width: 40,
            ),
            FloatingActionButton(
              onPressed: _getImage,
              tooltip: 'Open Gallary',
              child: Icon(Icons.add_photo_alternate),
            )
          ],
        ),
        body: isLoading
            ? Center(child: CircularProgressIndicator())
            : (_imageFile == null)
                ? Center(child: Text('no image selected'))
                : Center(
                    child: FittedBox(
                    child: SizedBox(
                      width: _image.width.toDouble(),
                      height: _image.height.toDouble(),
                      child: CustomPaint(
                        painter: FacePainter(_image, _faces),
                      ),
                    ),
                  )));
  }

  _getFromCamera() async {
    PickedFile pickedFile = await ImagePicker().getImage(
      source: ImageSource.camera,
      maxWidth: 1800,
      maxHeight: 1800,
    );
    setState(() {
      isLoading = true;
    });
    final image = FirebaseVisionImage.fromFile(File(pickedFile.path));
    final faceDetector = FirebaseVision.instance.faceDetector();
    List<Face> faces = await faceDetector.processImage(image);

    if (mounted) {
      setState(() {
        _imageFile = File(pickedFile.path);
        _faces = faces;
        _loadImage(File(pickedFile.path));
      });
    }
  }

  _getImage() async {
    final imageFile = await picker.getImage(source: ImageSource.gallery);
    setState(() {
      isLoading = true;
    });

    final image = FirebaseVisionImage.fromFile(File(imageFile.path));
    final faceDetector = FirebaseVision.instance.faceDetector();
    List<Face> faces = await faceDetector.processImage(image);

    if (mounted) {
      setState(() {
        _imageFile = File(imageFile.path);
        _faces = faces;
        _loadImage(File(imageFile.path));
      });
    }
  }

  _loadImage(File file) async {
    final data = await file.readAsBytes();
    await decodeImageFromList(data).then((value) => setState(() {
          _image = value;
          isLoading = false;
        }));
  }
}

class FacePainter extends CustomPainter {
  final ui.Image image;
  final List<Face> faces;
  final List<Rect> rects = [];

  FacePainter(this.image, this.faces) {
    for (var i = 0; i < faces.length; i++) {
      rects.add(faces[i].boundingBox);
    }
  }

  @override
  void paint(ui.Canvas canvas, ui.Size size) {
    final Paint paint = Paint()
      ..style = PaintingStyle.stroke
      ..strokeWidth = 2.0
      ..color = Colors.yellow;

    canvas.drawImage(image, Offset.zero, Paint());
    for (var i = 0; i < faces.length; i++) {
      canvas.drawRect(rects[i], paint);
    }
  }

  @override
  bool shouldRepaint(FacePainter old) {
    return image != old.image || faces != old.faces;
  }
}
