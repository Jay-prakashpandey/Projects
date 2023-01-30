import 'package:flutter/material.dart';

class ResultPage extends StatelessWidget {
  final int _resultScore;
  final void Function() resetHandler;

  ResultPage(this._resultScore, this.resetHandler);

  String get resultPhrase {
    String resultText;
    if (_resultScore <= 8) {
      resultText = 'You are awesome and innocent!';
    } else if (_resultScore <= 12) {
      resultText = 'Pretty likeable!';
    } else if (_resultScore <= 16) {
      resultText = 'You are ... strange?!';
    } else {
      resultText = 'You are so bad!';
    }
    return resultText;
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        children: <Widget>[
          Text(
            resultPhrase,
            style: const TextStyle(fontSize: 36, fontWeight: FontWeight.bold),
            textAlign: TextAlign.center,
          ),
          ElevatedButton.icon(
            icon: const Icon(Icons.lock_reset_outlined),
            label: const Text(
              'Restart Quiz!',
            ),
            onPressed: resetHandler,
          ),
        ],
      ),
    );
  }
}
