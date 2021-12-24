//@dart=2.9
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/rendering.dart';
import 'package:google_ml_kit/google_ml_kit.dart';
import 'package:image_picker/image_picker.dart';
import 'package:flutter/widgets.dart';
import 'package:vertical_card_pager/vertical_card_pager.dart';
//import 'package:face_mask/live_detect/ui/home_view.dart';
import 'dart:io';
import 'dart:ui' as ui;

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData.dark(),
      title: 'Face_Mask_Detection',
      home: Face_Home(),
    );
  }
}

class Face_Home extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final List<String> titles = [
      "Project about",
      "Jay Prakash pandey",
      "Rohan Das",
      "Abhishek Anand",
      "Swapnamoy Das",
    ];

    final List<Widget> images = [
      Container(
          color: Colors.blueGrey,
          child: Expanded(
              child: Text(
            'In this ongoing era of pandemic, wearing a “Face Mask” is the need of the hour as it is a proven medical equipment to resist the spread of SARS-CoV-2 Virus, also known as Coronavirus, through human interaction. But despite of the COVID norms, many people are still loitering around without wearing a face mask properly. It is an absolute necessity to track those people as they are the most potential super-spreaders of COVID-19.\nTo resolve this issue, we have come up with an innovative yet cost-effective module for "Mass Face Mask Detection" using the principles of Computer Vision. Using that model, we use a live-streaming camera and ffmpeg module, in order to run the module on a larger number of people in a certain area and to successfully detect the number of mask-less people & track them accordingly. Our application will also upload that real-time data constantly into the server to track and monitor the real-time data of a particular area. This project would not only benefit the masses as it will successfully detect the mask-less people in a large locality and thus significantly control the spread of the pandemic in that certain area, but also it would also help the administration to ease the hectic process of tracking each and every mask-less person manually. Thus, our project is focused on both the domains of efficiency and cost-effectiveness to ensure maximum safety of an area by curbing the spread of SARS-CoV-2 (COVID-19) Virus.\n\nKeywords: Tensorflow, ffmpeg, Computer Vision, COVID-19, Face Mask',
            //textScaleFactor: 1.3,
            textAlign: TextAlign.center,
          ))),
      Container(
        color: Colors.red,
        child: Column(
          children: [
            Expanded(
              flex: 2,
              child: Image(
                image: AssetImage("assets/j.jpeg"),
              ),
            ),
            Expanded(
                flex: 1,
                child: Text(
                  'Jay Pakash Pandey\njayprakashpandey47b@gmail.com\nB.tech IT 3rd year \nInstitute of engineering and management',
                  textScaleFactor: 1.3,
                  textAlign: TextAlign.center,
                ))
          ],
        ),
      ),
      Container(
        color: Colors.cyan,
        child: Column(
          children: [
            Expanded(
              flex: 2,
              child: Image(
                image: AssetImage("assets/rohan.jpeg"),
              ),
            ),
            Expanded(
                flex: 1,
                child: Text(
                  'Rohan Das\njrohan.das.official.01@gmail.com\nBtech IT 3rd year \nInstitute of engineering and management',
                  textScaleFactor: 1.3,
                  textAlign: TextAlign.center,
                ))
          ],
        ),
      ),
      Container(
        color: Colors.cyanAccent,
        child: Column(
          children: [
            Expanded(
              flex: 2,
              child: Image(
                image: AssetImage("assets/abhi.jpeg"),
              ),
            ),
            Expanded(
                flex: 1,
                child: Text(
                  'Abhishek Anand\nabhianand2308@gmail.com\nB.tech IT 3rd year \nInstitute of engineering and management',
                  textScaleFactor: 1.3,
                  textAlign: TextAlign.center,
                ))
          ],
        ),
      ),
      Container(
        color: Colors.black,
        child: Column(
          children: [
            Expanded(
              flex: 2,
              child: Image(
                image: AssetImage("assets/swapnamoy.jpeg"),
              ),
            ),
            Expanded(
                flex: 1,
                child: Text(
                  'Swapnamoy Das\ne.33.swapnamoydas@gmail.com\nB.tech IT 3rd year \nInstitute of engineering and management',
                  textScaleFactor: 1.3,
                  textAlign: TextAlign.center,
                ))
          ],
        ),
      ),
    ];

    return Scaffold(
      appBar: AppBar(
        title: Text('Face Mask Detection'),
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
                      backgroundColor: Colors.amber,
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
                          children: <Widget>[
                            Expanded(
                              child: Container(
                                child: VerticalCardPager(
                                  textStyle: TextStyle(
                                      color: Colors.white.withOpacity(0.4)),
                                  titles: titles,
                                  images: images,
                                  onPageChanged: (page) {},
                                  align: ALIGN.CENTER,
                                  onSelectedItem: (index) {},
                                ),
                              ),
                            ),
                          ],
                        ),
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
  final faceDetector = GoogleMlKit.vision.faceDetector();
  //late Classifier _classifier;
  //final interperter = Interpreter.fromAsset('mask_detection.tflite');
  var _imageFile;
  List<Face> _faces = [];
  bool isLoading = false;
  ui.Image _image;
/*
  @override
  void initState() {
    super.initState();
    final _classifier = Classifier;
  }
  @override
  void dispose(){
    super.dispose();
    //_classifier.close();

  }
*/
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        floatingActionButton: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            FloatingActionButton(
              onPressed: _getFromCamera,
              tooltip: 'Open Camera',
              child: Icon(Icons.add_a_photo_sharp),
            ),
            SizedBox(
              width: 40,
            ),
            FloatingActionButton(
              onPressed: _getImage,
              tooltip: 'Open Gallary',
              child: Icon(Icons.add_photo_alternate),
            ),
            SizedBox(width: 20),
            FloatingActionButton(
                tooltip: 'Live Camera',
                child: Icon(Icons.camera),
                onPressed:(){}, /*{
                  Navigator.push(
                    context,
                    MaterialPageRoute<void>(builder: (BuildContext context) {
                      return MaterialApp(
                        title: 'Object Detection TFLite',
                        theme: ThemeData(
                          primarySwatch: Colors.blue,
                          visualDensity: VisualDensity.adaptivePlatformDensity,
                        ),
                        home: HomeView(),
                      );
                    }),
                  );
                }*/),
          ],
        ),
        body: isLoading
            ? Center(child: CircularProgressIndicator())
            : (_imageFile == null)
                ? Center(
                    child: Text(
                        'In this ongoing era of pandemic, wearing a “Face Mask” is the need of the hour as it is a proven medical equipment to resist the spread of SARS-CoV-2 Virus, also known as Coronavirus, through human interaction. But despite of the COVID norms, many people are still loitering around without wearing a face mask properly. It is an absolute necessity to track those people as they are the most potential super-spreaders of COVID-19.\n\nTo resolve this issue, we have come up with an innovative yet cost-effective module for "Mass Face Mask Detection" using the principles of Computer Vision. First, we have created a trained model based on AI using Tensorflow, Keras & Mobilenet in order to distinguish between a masked and a mask-less person.\n\nWe have trained our built model more and more efficiently to successfully detect faces with precision. Then, using that model, we use a live-streaming camera and ffmpeg module, in order to run the module on a larger number of people in a certain area and to successfully detect the number of mask-less people & track them accordingly. Our application will also upload that real-time data constantly into the server to track and monitor the real-time data of a particular area. This project would not only benefit the masses as it will successfully detect the mask-less people in a large locality and thus significantly control the spread of the pandemic in that certain area, but also it would also help the administration to ease the hectic process of tracking each and every mask-less person manually. Thus, our project is focused on both the domains of efficiency and cost-effectiveness to ensure maximum safety of an area by curbing the spread of SARS-CoV-2 (COVID-19) Virus.\n\n\nKeywords: Tensorflow, ffmpeg, Computer Vision, COVID-19, Face Mask'))
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
    if (pickedFile == Null) {
      setState(() {
        isLoading = true;
      });
    }
    final image = InputImage.fromFile(File(pickedFile.path));
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
    final imageFile = await ImagePicker().getImage(
      source: ImageSource.gallery,
      maxWidth: 1800,
      maxHeight: 1800,
    );
    if (imageFile == Null) {
      setState(() {
        isLoading = true;
      });
    }

    final image = InputImage.fromFile(File(imageFile.path));
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
  int total = 0;

  FacePainter(this.image, this.faces) {
    print(faces.length);
    total = faces.length;
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
