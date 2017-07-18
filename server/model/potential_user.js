var mongoose = require('mongoose');


var Schema = mongoose.Schema;

/**
 * This model represnets the potential users that are authorized by a manaager.
 * @type {mongoose.Schema}
 */
var potentialUserSchema = new Schema({

    code: {type: String},

    first_name: {type: String},

    last_name: {type: String},

    entity: {type: String},

    key: {type: String},

});

mongoose.model('potential_user', potentialUserSchema);