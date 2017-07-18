var mongoose = require('mongoose');


var Schema = mongoose.Schema;

/**
 * This model represents the pending 'together shifts'.
 * @type {mongoose.Schema}
 */
var pending_shiftSchema = new Schema({

    id : {type: String},

    start: {type: String},

    end: {type: String},

    psycho_id: {type: String},

    psycho_deviceId: {type: String},

    psycho_name: {type: String},

    psycho_mail: {type: String},

    approved: {type: Boolean, default: false}

});

mongoose.model('pending_shift', pending_shiftSchema);