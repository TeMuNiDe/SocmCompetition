var functions = require('firebase-functions');
var admin = require('firebase-admin');

var serviceAccount = require("./socmadmin.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://socmcompetetion.firebaseio.com"
   
});
 console.log('Initialized');

var db = admin.database();

exports.addToDatabase = functions.https.onRequest((req,res)=>{
  var data = req.body;
    
  
    
  var address = req.get('address');
    db.ref("submissions").child(address).set(JSON.parse(data));  
  res.status(200).send(JSON.parse(data));
});