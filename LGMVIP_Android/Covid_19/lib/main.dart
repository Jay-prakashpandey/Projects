import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:project01/citysearch.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData.dark(),
      title: 'covid-19',
      home: covid_home(),
    );
  }
}

class covid_home extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('covid-19'),
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
      body: HomeScreen(),
    );
  }
}

class HomeScreen extends StatefulWidget {
  const HomeScreen({Key? key}) : super(key: key);

  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  String active = '0',
      state = "Select a state",
      distric = "Select the city",
      confirmed = '0',
      migratedother = '0',
      deceased = '0',
      recovered = '0';

  Future<void> readJson(s) async {
    final String response =
        await rootBundle.loadString('files/state_district_wise.json');
    final data = await json.decode(response);
    setState(() {
      state = s[0];
      distric = s[1];
      active = (data["$state"]["districtData"]["$distric"]["active"]).toString();
      confirmed = (data["$state"]["districtData"]["$distric"]["confirmed"]).toString();
      migratedother =
          (data["$state"]["districtData"]["$distric"]["migratedother"]).toString();
      deceased = (data["$state"]["districtData"]["$distric"]["deceased"]).toString();
      recovered = (data["$state"]["districtData"]["$distric"]["recovered"]).toString();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      //mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: <Widget>[
        FractionallySizedBox(
          child: ElevatedButton.icon(
            onPressed: () async {
              final result = await Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => MyHomePage()),
              );
              readJson(result);
              //print(result);
            },
            icon: Icon(
              Icons.location_city,
              color: Colors.amber,
            ),
            label: Text(
              '\nState: $state \n--------------------------------\n    City:  $distric\n',
              style: TextStyle(
                fontWeight: FontWeight.bold,
                fontSize: 20,
              ),
            ),
          ),
          widthFactor: 1,
        ),
        Expanded(
          child: Container(
            child: Image.asset('files/COVID.jpg'),
          ),
        ),
        Expanded(
          flex: 1,
          child: Container(
            child: Row(
              children: <Widget>[
                Expanded(
                  child: Container(
                    width: 30,
                    height: 60,
                    color: Colors.black12,
                    child: Text(
                      'Active\n$active\n',
                      style: TextStyle(
                        color: Colors.red,
                        fontWeight: FontWeight.bold,
                        fontSize: 25,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ),
                Expanded(
                    child: SizedBox(
                  width: 10,
                )),
                Expanded(
                  child: Container(
                    width: 30,
                    height: 60,
                    color: Colors.black26,
                    child: Text(
                      'Confirmed\n$confirmed\n',
                      style: TextStyle(
                        color: Colors.red,
                        fontWeight: FontWeight.bold,
                        fontSize: 25,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
        Expanded(
          flex: 1,
          child: Container(
            child: Row(
              children: <Widget>[
                Expanded(
                  child: Container(
                    width: 30,
                    height: 60,
                    color: Colors.yellow,
                    child: Text(
                      'Migratedother\n$migratedother\n',
                      style: TextStyle(
                        color: Colors.red,
                        fontWeight: FontWeight.bold,
                        fontSize: 20,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ),
                Expanded(
                  child: Container(
                    width: 30,
                    height: 60,
                    color: Colors.cyan,
                    child: Text(
                      'Recovered\n$recovered\n',
                      style: TextStyle(
                        color: Colors.red,
                        fontWeight: FontWeight.bold,
                        fontSize: 25,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ),
                Expanded(
                  child: Container(
                    width: 30,
                    height: 60,
                    color: Colors.green,
                    child: Text(
                      'Deceased\n$deceased\n',
                      style: TextStyle(
                        color: Colors.red,
                        fontWeight: FontWeight.bold,
                        fontSize: 25,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
        SizedBox(
          child: Text(
            'DELTA\n-------',
            style: TextStyle(
              color: Colors.blueGrey,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
        Expanded(
          flex: 1,
          child: Container(
            child: Row(
              children: <Widget>[
                Expanded(
                  child: Container(
                    width: 30,
                    height: 60,
                    color: Colors.lightBlue,
                    child: Text(
                      'Confirmed\n0\n',
                      style: TextStyle(
                        color: Colors.red,
                        fontWeight: FontWeight.bold,
                        fontSize: 20,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ),
                Expanded(
                  child: Container(
                    width: 30,
                    height: 60,
                    color: Colors.cyan,
                    child: Text(
                      'Recovered\n0\n',
                      style: TextStyle(
                        color: Colors.red,
                        fontWeight: FontWeight.bold,
                        fontSize: 25,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ),
                Expanded(
                  child: Container(
                    width: 30,
                    height: 60,
                    color: Colors.green,
                    child: Text(
                      'Deceased\n0\n',
                      style: TextStyle(
                        color: Colors.red,
                        fontWeight: FontWeight.bold,
                        fontSize: 25,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}
