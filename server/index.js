const express        = require('express');
const bodyParser     = require('body-parser');
const app            = express();
const restfulMongoose =require('restful-mongoose');
const bodyparser  =require('body-parser');
//Import the mongoose module
var mongoose = require('mongoose');

//Set up default mongoose connection
var mongoDB = 'mongodb://127.0.0.1/api-contacts';
mongoose.connect(mongoDB);
// Get Mongoose to use the global promise library
mongoose.Promise = global.Promise;
//Get the default connection
var db = mongoose.connection;

//Bind connection to error event (to get notification of connection errors)
db.on('error', console.error.bind(console, 'MongoDB connection error:'));


// any mongoose Model:
const Contact = require('./models/contact');
const Message = require('./models/message');

const FCM = require('fcm-node');
const serverKey = process.env.repertoireServerKey; //put your server key here
const fcm = new FCM(serverKey);

app.use(bodyparser.urlencoded());
// create and export a Router, mount it anywhere via .use()
app.use('/contact', restfulMongoose('contact',Contact));
app.get('/contact2', function(req, res) {
    Contact.find(function(err, contacts){
	res.send(JSON.stringify(contacts));
    });
});
app.use('/messages',restfulMongoose('message',Message));
app.get('/messages2', function(req, res){
    Message.find(function(err,messages){
	res.send(JSON.stringify(messages));
    });
});
app.post('/messages2', function(req, res){
    const messageParams = { to_id: req.body.to_id, from_id: req.body.from_id, body: req.body.body  }
    Message.create(messageParams, function(err, message){
	Contact.findOne({ _id: message.to_id }, function(err, recipient){
	    console.log(`recipient: ${recipient.first_name} token: ${recipient.gcm_token}`)
	    const pushMessage = { //this may vary according to the message type (single recipient, multicast, topic, et cetera)
		//console.log(`recipient: ${recipient.first_name}`)
		to: recipient.gcm_token,
		collapse_key: message._id,

		notification: {
		    title: message.body,
		    body: message.body
		},

		data: message.toJSON()
	    };
	    fcm.send(pushMessage, function(err,response){
		if (err) {
		    console.log("Something has gone wrong!"+err);
		} else {
		    console.log("Successfully sent with response: ", response);
		}
	    })
	});
	res.send(JSON.stringify(message));
    });
});
const port = 1434;
app.listen(port, () => {
    console.log('We are live on ' + port);
});
