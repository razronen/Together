var mongoose = require('mongoose');
var validators = require('mongoose-validators');


var Schema = mongoose.Schema;

/**
 * Used to represent the quote model.
 * @type {mongoose.Schema}
 */
var quoteSchema = new Schema({

    num: {type: Number},

    content: {type: String},

    creator: {type: String},

});

mongoose.model('quote', quoteSchema);