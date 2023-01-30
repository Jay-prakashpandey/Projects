import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:quiz_app/question.dart';


class firebaseCrud {
  List<question> _list = [
    question("this is sample question", ["a", "b", "c", "d"], 0)
  ];

  firebaseCrud() {
    getUsers();
  }

  List<question> getlist() {
    return _list;
  }

  getUsers() async {
    var collection = FirebaseFirestore.instance.collection('database_manager');
    var querySnapshot = await collection.get();
    for (var queryDocumentSnapshot in querySnapshot.docs) {
      Map<String, dynamic> data = queryDocumentSnapshot.data();
      question q = question.fromMap(data);
      _list.add(q);
    }
  }
}