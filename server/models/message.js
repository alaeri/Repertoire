//Require Mongoose
var mongoose = require('mongoose');

//Define a schema
var Schema = mongoose.Schema;

var MessageSchema = new Schema(
    {
	from_id: String,
	to_id: String,
	body: String,
	date: { type: Date, default: Date.now },
    });

module.exports = mongoose.model('Message', MessageSchema );