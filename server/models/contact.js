//Require Mongoose
var mongoose = require('mongoose');

//Define a schema
var Schema = mongoose.Schema;

var ContactSchema = new Schema({
	access_token: String,
	picture: String,
	first_name: String,
	last_name: String,
	living: Boolean,
	gcm_token: String,
	email: String,
	updated: { type: Date, default: Date.now },
	phone_number: String,
	
	
	age: { type: Number, min: 0, max: 200, required: false },
    })

module.exports = mongoose.model('Contact', ContactSchema );