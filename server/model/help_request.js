var mongoose = require('mongoose');

var Schema = mongoose.Schema;

/**
 * This model represents the help request.
 * @type {mongoose.Schema}
 */
var helpRequestSchema = new Schema({

    num: {type: Number},

    child_id: {type: String},

    child_name: {type: String},

    time: {type: String},

    message: {type: String},

    answered_by: {type: String, default: ""}

});

mongoose.model('help_request', helpRequestSchema);