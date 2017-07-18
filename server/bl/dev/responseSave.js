var mongoose = require('mongoose');
var response = mongoose.model('response');
var responseSaveBL = require('../../bl/chat/responseSave');

/**
 * Save response
 * @param _child_id - conversation id
 * @param _num - num of response in converstion
 * @param _time - time of response creation
 * @param _message - content of response
 * @param _author_id - id
 * @param _author_name - name
 * @param _author_entity - entity
 * @param next - route function.
 */
exports.saveResponse = function(_child_id,_num,_time,_message,_author_id,_author_name,_author_entity, next) {
    var r = { child_id: _child_id
        ,num: _num
        ,time : _time
        ,message: _message
        ,author_id: _author_id
        ,link: ""
        ,image: ""
        ,author_name: _author_name
        ,author_entity : _author_entity}
    responseSaveBL.saveResponse(r, function(err,data){
        next(err,data);
    })

}