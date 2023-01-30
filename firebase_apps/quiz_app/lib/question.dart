import "package:flutter/material.dart";

class question {
    late String ques;
    late List<dynamic> option;
    late int answer;

    question(@required String q,@required List<String> o,@required int ans){
        ques=q;
        option=o;
        answer=ans;
    }

    question.fromMap(Map<String,dynamic>map){
        ques=map['question'];
        option=map['option'];
        answer=map['answer'];
    }

    List<dynamic> getOption(){return option;}
    String getQuestion(){return ques;}
}

