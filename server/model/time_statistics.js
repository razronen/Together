var mongoose = require('mongoose');

var Schema = mongoose.Schema;

/**
 * Used to represent the day statistics,
 *  generaly, each schema is saved by one property.
 * @type {mongoose.Schema}
 */
var timeStatisticsSchema = new Schema({

    public_post : {type: Number},

    private_post : {type: Number},

    response: {type: Number},

    chat_started: {type: Number},

    chat_responses: {type: Number},

    inserted_to_quotes: {type: Number},

    user_sign_up: {type: Number},

    user_delete: {type: Number},

    psychologist_action: {time : {type: Number}, name: {type: String}, id: {type: String}},

    child_action: {time : {type: Number}, name: {type: String}, id: {type: String}},


});

mongoose.model('time_statistics', timeStatisticsSchema);