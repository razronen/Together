var mongoose = require('mongoose');
var updateModel = mongoose.model('update');
var potentialUserModel = mongoose.model('potential_user');
var FCM = require('fcm-node');
var fcm = new FCM(require('../../package.json').server_key);
var rand = require("generate-key");

/**
 * Saving a login requeust - by mail or hone.
 * Adding it to updates feeds.
 * @param body - req contentn.
 * @param next - route function.
 */
exports.saveLoginRequest = function(body,next) {
    if(body.hasOwnProperty('mail')){
        checkIfAlreaddyExists(body.code, "mail", body.mail, body.first_name,body.last_name, body.entity, function(err, exists){
            if(err==null && exists==false){
                saveToUpdates(body.code, "mail", body.mail, body.first_name,body.last_name, body.entity);
            }
        });
    } else if(body.hasOwnProperty('phone')){
        checkIfAlreaddyExists(body.code, "phone", body.phone, body.first_name, body.last_name, body.entity,function(err, exists) {
            if(err==null && exists==false) {
                saveToUpdates(body.code, "phone", body.phone, body.first_name, body.last_name, body.entity);
            }
        });
    }
    next(null,"");
}

/**
 * Checks if a registreation request is already exists by this information.
 * @param code  -of user
 * @param way-of user
 * @param contact-of user
 * @param first_name-of user
 * @param last_name-of user
 * @param entity-of user
 * @param next - callback function.
 */
var checkIfAlreaddyExists = function(code, way, contact, first_name, last_name, entity, next){
    var j = '{ "code": "'+ code + '" , "content" : "' +
        contact + '", "first_name": "' + first_name + '", "last_name": "' + last_name
        + '", "entity": "' + entity + '", "way": "' + way + '"  }';
    updateModel.findOne({json: j}, function(err,data){
        if(err || data==undefined || data.length==0){
            next(err,false);
        } else {
            next(err,true);
        }
    });
}

/**
 * Saving the registreation request to update feeds.
 * @param code-of user
 * @param way-of user
 * @param contact-of user
 * @param first_name-of user
 * @param last_name-of user
 * @param entity-of user
 */
var saveToUpdates = function(code, way, contact, first_name, last_name, entity){
    //checks if allready existss
    var newUserUpdate = {
        num: 1,
        time: (new Date).getTime(),
        type: 'USER_UPDATE',
        json: '{ "code": "'+ code + '" , "content" : "' +
        contact + '", "first_name": "' + first_name + '", "last_name": "' + last_name
        + '", "entity": "' + entity + '", "way": "' + way + '"  }'
    }
    updateModel.find()
        .sort('num')
        .exec(function(err,data1){
            if(err) next(err)
            else {
                if (data1.length!=0) {
                    newUserUpdate.num = data1[data1.length -1].num+1;
                }
                var updateInstance = new updateModel(newUserUpdate);
                updateInstance.save();
            }}
        );
}

/**
 * Approve the user.
 * Editing update feeds and sending message to the request author.
 * @param body content
 * @param next - callback function.
 */
exports.approvedUser = function(body,next) {
    editUpdate(body.num, true);
    SendUpdatedMessage("ביחד", body.code, "ברוך/ה הבא/ה", body.first_name, body.last_name, body.code,body.entity, function(err, key){
        if(err) next(err,undefined);
        else {
            //save key to registed potential child
            SaveToPotentialUsers(body.code, body.first_name, body.last_name, body.entity, key);
            next(err,key);
        }
    })
}

/**
 * Replcaing the json of this update by the approve information
 * @param _num - of update.
 * @param _approve - true/false
 */
var editUpdate = function(_num, _approve){
    updateModel.findOne({num: _num}, function(err,data){
        if(err) return;
        else {
            var j = data.json;
            j = j.substring(0, j.lastIndexOf("}")-1) + ',"approve": "' + _approve + '" }';
            var conditions = { num: _num }
                , update = { json: j  }
                , options = { multi: true };

            updateModel.update(conditions, update, options, function(err, affected, resp){
                return;
            });
        }
    })

}

/**
 * Adding a potential user to the device Id.
 * @param _code-of user
 * @param _first_name-of user
 * @param _last_name-of user
 * @param _entity-of user
 * @param _key to be saved for later(when regist a profile).
 * @constructor
 */
var SaveToPotentialUsers = function(_code,_first_name,_last_name, _entity,_key){
    var newPotentialUser = {
        code: _code,
        first_name: _first_name,
        last_name: _last_name,
        entity: _entity,
        key: _key
    }
    var potentialUserInstance = new potentialUserModel(newPotentialUser);
    potentialUserInstance.save();
}

/**
 * Sending update message the author request by firebase.
 * @param author
 * @param _to - deviceId
 * @param message - content
 * @param _first_name
 * @param _last_name
 * @param _code - deviceId
 * @param _entity - of user.
 * @param next - callback function.
 * @constructor
 */
var SendUpdatedMessage = function(author, _to, message, _first_name, _last_name,_code,_entity, next){
    var _key = rand.generateKey().toString();
    var message = {
        to: _to,
        collapse_key: 'AIzaSyB_xvM52ngkCFS_gzgnTzaQtJWz4dTe2ws',

        notification: {
            title: author,
            body: message
        },

        data: {  //you can send only notification or only data(or include both)
            approve: true,
            key: _key,
            first_name: _first_name,
            last_name: _last_name,
            code: _code,
            entity: _entity
        }
    }
    fcm.send(message, function(err, response){
        if (err) {
            next(err,undefined);
            console.log("Something has gone wrong!");
        } else {
            next(err,_key);
            console.log("Successfully sent with response: ", response);
        }
    });
}
