var mongoose = require('mongoose')

var Schema = mongoose.Schema;

/**
 * Used to represent the response model.
 * @type {mongoose.Schema}
 */
var responseSchema = new Schema({

    child_id: {type: String},

    num: {type: Number},

    time:{type: Number},

    message: { type: String},

    image: {type: String},

    image_x: {type: String},

    image_y: {type: String},

    link: {type: String},

    author_id: {type:String},

    author_name: {type: String},

    author_entity: {type: String}
});

mongoose.model('response', responseSchema);