import 'dart:io';
import 'package:tflite/tflite.dart';
import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:google_ml_kit/google_ml_kit.dart';
import 'package:face_mask/camera_view.dart';
import 'package:image/image.dart' as img;
import 'package:face_mask/main.dart';

class FaceDetectorView extends StatefulWidget {
  @override
  _FaceDetectorViewState createState() => _FaceDetectorViewState();
}

class _FaceDetectorViewState extends State<FaceDetectorView> {
  final List<bool>? isBusy;
  CustomPaint? customPaint;
  final List<Rect>? _rects = [];
  List<Face>? _faces;
  img.Image? faceCrop, originalImage;
  List? recognitions;
  int x = 0, y = 0, w = 0, h = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: const Text('Live Detection'),
          centerTitle: true,
          leading: IconButton(
            icon: const Icon(Icons.arrow_back_ios_sharp),
            onPressed: () {
              Navigator.pop(context);
            },
          ),
        ),
        body: CameraView(
          onImage: (inputImage, image) {
            processImage(inputImage, image);
          },
          initialDirection: CameraLensDirection.front, customPaint: customPaint
        ));
  }

  img.Image _convertedToImage(CameraImage image) {
    var tmpimg = img.Image(image.width, image.height); // Create Image buffer
    const int hexFF = 0xFF000000;
    final int uvyButtonStride = image.planes[1].bytesPerRow;
    final int? uvPixelStride = image.planes[1].bytesPerPixel;
    for (int x = 0; x < image.width; x++) {
      for (int y = 0; y < image.height; y++) {
        final int uvIndex = uvPixelStride! * (x / 2).floor() +
            uvyButtonStride * (y / 2).floor();
        final int index = y * image.width + x;
        final yp = image.planes[0].bytes[index];
        final up = image.planes[1].bytes[uvIndex];
        final vp = image.planes[2].bytes[uvIndex];
        // Calculate pixel color
        int r = (yp + vp * 1436 / 1024 - 179).round().clamp(0, 255);
        int g = (yp - up * 46549 / 131072 + 44 - vp * 93604 / 131072 + 91)
            .round()
            .clamp(0, 255);
        int b = (yp + up * 1814 / 1024 - 227).round().clamp(0, 255);
        // color: 0x FF  FF  FF  FF
        //           A   B   G   R
        tmpimg.data[index] = hexFF | (b << 16) | (g << 8) | r;
      }
    }
    return tmpimg;
  }

  Future<void> processImage(InputImage inputImage, CameraImage image) async {
    if (isBusy) return;
    isBusy = true;
    _rects!.clear();
    _faces = await (faceDetector.processImage(inputImage));
    if (mounted && _faces!.isNotEmpty) {
      originalImage = _convertedToImage(image);
      for (int i = 0; i < _faces!.length; i++) {
        _rects!.add(_faces![i].boundingBox);
        x = _faces![i].boundingBox.left.toInt();
        y = _faces![i].boundingBox.top.toInt();
        w = _faces![i].boundingBox.width.toInt();
        h = _faces![i].boundingBox.height.toInt();
        faceCrop = img.copyCrop(originalImage!, x, y, w, h);
        File(tempPath!).writeAsBytesSync(img.encodePng(faceCrop!), flush: true);
        recognitions = await Tflite.runModelOnImage(
          path: tempPath!,
          numResults: 1,
          threshold: 0.05,
          imageMean: 127.5,
          imageStd: 127.5,
        );
        if (recognitions![0]["label"] == "mask") {
          customPaint=CustomPaint(
            painter: livePainter(true,_rects![i],inputImage.inputImageData!.size,inputImage.inputImageData!.imageRotation),
          );
        }else {
          customPaint = CustomPaint(
            painter: livePainter(false, _rects![i], inputImage.inputImageData!.size,inputImage.inputImageData!.imageRotation),
          );
        }
        setState(() {});
    }/*for multi faces 
    else {
          customPaint=CustomPaint(
            painter: livePainter(true,_rects![i],inputImage.inputImageData!.size,inputImage.inputImageData!.imageRotation),
          );
        }
        setState(() {});
      }*/
    } else {
      customPaint = null;
      setState(() {});
    }
    isBusy = false;
    setState(() {});
  }
}
