import 'package:flutter/material.dart';
import 'package:quiz_app/question.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:quiz_app/firebaseCrud.dart';
import 'firebase_options.dart';
import 'package:quiz_app/result.dart';

late List<question> _questionList;
List<int> _selectedList=[];

// void main() {
//   runApp(const MyApp());
// }
Future main() async{
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  _questionList=firebaseCrud().getlist();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Quiz App',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Quiz home page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});
  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _quesIndex = 0;
  int _totalScore=0;

  int _groupValue(){
    if(_quesIndex==_selectedList.length)  {
      _selectedList.add(-1);
      return _selectedList.last;
    }
    return _selectedList[_quesIndex];
  }
  void _decrementCounter() {
    setState(() {
      if(_quesIndex>0) _quesIndex--;
    });
  }
  void _incrementCounter() {
    setState(() {
      if(_quesIndex<_questionList.length-1) _quesIndex++;
    });
  }
  void _submit(){
    for(int i=0;i<_quesIndex;i++){
      if(_selectedList[i]==_questionList[i].answer){
        _totalScore++;
      }
    }
    setState(() {
      _quesIndex++;
    });
  }
  void _resetQuiz() {
    _selectedList.clear();
    setState(() {
      _quesIndex = 0;
      _totalScore = 0;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body:_quesIndex==_questionList.length?ResultPage(_totalScore, _resetQuiz):
      Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
                'Q. ${_quesIndex+1} ${_questionList[_quesIndex].getQuestion()}',
              style: const TextStyle(
                fontSize: 28,
                fontWeight: FontWeight.bold,
              ),
            ),
            for(int i=0;i<_questionList[_quesIndex].getOption().length;i++)
              ListTile(
                title: Text(_questionList.elementAt(_quesIndex).getOption().elementAt(i)),
                leading: Radio(
                  value: i,
                  groupValue: _groupValue(),
                  activeColor: const Color(0xFF6200EE),
                  onChanged: i == _questionList[_quesIndex].getOption().length ? null : (int? value) {
                    setState(() {
                      _selectedList[_quesIndex] = value!;
                    });
                  },
                ),
              ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                ElevatedButton.icon(
                  icon: const Icon(Icons.skip_previous),
                    onPressed: _decrementCounter,
                    label: const Text(" Prev ")
                ),
                const SizedBox(
                  width: 25,
                ),
                ElevatedButton.icon(
                    icon: Icon(_quesIndex+1==_questionList.length?Icons.outbond:Icons.skip_next),
                    onPressed: _quesIndex+1==_questionList.length?_submit:_incrementCounter,
                    label: const Text(" Save&Next "),
                ),
              ],
            )
          ],
        ),
      ),
    );
  }
}
