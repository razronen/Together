var mongoose = require('mongoose')

var Schema = mongoose.Schema;

/**
 * Used to represents the update model.
 * @type {mongoose.Schema}
 */
var updateSchema = new Schema({

    num: {type: Number},

    time:{type: Number},

    type:{type: String},

    json: {type: String}
});

mongoose.model('update', updateSchema);