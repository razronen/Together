var mongoose = require('mongoose');
var shiftModel = mongoose.model('pending_shift');
var fs = require('fs');
var readline = require('readline');
var google = require('googleapis');
var googleAuth = require('google-auth-library');
var calendar = google.calendar('v3');
// If modifying these scopes, delete your previously saved credentials
// at ~/.credentials/calendar-nodejs-quickstart.json
var SCOPES = ['https://www.googleapis.com/auth/calendar'];
var TOKEN_DIR = (process.env.HOME || process.env.HOMEPATH ||
    process.env.USERPROFILE) + '/';
var TOKEN_PATH = TOKEN_DIR + 'client_secret.json';
var FCM = require('fcm-node');
var fcm = new FCM(require('../../package.json').server_key);
var updateModel = mongoose.model('update');

/**
 * Gets all the approved shifts of certain psycholgist.
 * @param _id - id of psychologist.
 * @param next - route function
 */
exports.getApprovedShiftByPsychoId = function(_id, next){
    shiftModel.find({psycho_id: _id, approved: true},function(err,data){
        if(err) next(err,data);
        else {
            next(err,data);
        }
    })
}

/**
 * Get a specifc shift of psychologist.
 * @param _id - of psychologist.
 * @param next - route function
 */
exports.getShift = function(_id, next){
    shiftModel.find({id: _id},function(err,data){
        if(err) next(err,data);
        else {
            next(err,data);
        }
    })
}

/**
 * A manager may approve a psychologist shift by this function.
 * First edit the shift properties,
 * then updating update feeds,
 * then saving it to google calendar,
 * then sending prompt message to psychologist.
 * @param _id - of shift.
 * @param _num - of update shift request.
 * @param _approve - if it is approved or not.
 * @param next - route function.
 */
exports.approveShift = function(_id,_num, _approve, next) {
    var conditions = {id: _id}
        , update = { approved: _approve}
        , options = { new: true};
    shiftModel.findOneAndUpdate(conditions, update,options, function(err,data){
        if (err) next(err);
        else {
            if(_approve=="true") {
                editUpdate(_num, true);
                saveShiftToTogtherCalendar(data, function (err) {
                    if (err) next(err, data);
                    else {
                        sendApproveToPsycho(data, function (err, data1) {
                            if (err) next(err, data)
                            else {
                                next(err, data);
                            }
                        });
                    }
                });
            } else {
                editUpdate(_num, false);
                next(err,data);
            }
        }
    })
}

/**
 * Editing the shift request in updates feeds.
 * @param _num - update number.
 * @param _approve - if approved or not.
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
 * Sending the prompt message to psychologist.
 * @param shift - shift object.
 * @param next - callback function.
 */
var sendApproveToPsycho = function(shift, next){
    SendUpdatedMessage('אפליקצית ביחד',
        shift.psycho_deviceId,'משמרת התקבלה',shift.id, function(err,data){
        next(err,data);
    })

}

/**
 * Sending the updated message to psychologist registed device.
 * @param author - author
 * @param _to - device ID
 * @param message - prompt message
 * @param _shift_id - the id of DB object
 * @param next - callback function.
 * @constructor
 */
var SendUpdatedMessage = function(author, _to, message,_shift_id, next){
    var message = {
        to: _to,
        collapse_key: 'AIzaSyB_xvM52ngkCFS_gzgnTzaQtJWz4dTe2ws',

        notification: {
            title: author,
            body: message
        },

        data: {  //you can send only notification or only data(or include both)
            my_key: author,
            my_another_key: message,
            shift_id: _shift_id
        }
    }
    fcm.send(message, function(err, response){
        if (err) {
            next(err,null);
            console.log("Something has gone wrong! " + err);
        } else {
            next(err,response);
            console.log("Successfully sent with response: ", response);
        }
    });
}

/**
 * Updating the shift to google calendar.
 * @param max - max events.
 * @param next - route function.
 */
exports.shiftCalendar = function(max, next) {
    // Load client secrets from a local file.
    fs.readFile('client_secret.json', function processClientSecrets(err, content) {
        if (err)  {
            next('Error loading client secret file: ' + err);
        }
        else {
            // Authorize a client with the loaded credentials, then call the
            // Google Calendar API.
            authorize(JSON.parse(content), listEvents,max, function(err,data){
                next(err,data);
            });
        }
    });
}

/**
 * Save the specific together function.
 * @param shift - object.
 * @param next - callback function.
 */
var saveShiftToTogtherCalendar = function(shift,next){
    // Load client secrets from a local file.
    fs.readFile('client_secret.json', function processClientSecrets(err, content) {
        if (err)  {
            next('Error loading client secret file: ' + err);
        }
        else {
            // Authorize a client with the loaded credentials, then call the
            // Google Calendar API.
            authorize(JSON.parse(content), pushEvent, buildEvent(shift), function(err,data){
                next(err,data);
            });
        }
    });
}

/**
 * Building the google calendar event.
 * @param shift - object.
 * @returns {{summary: string, location: string, description: string, start: {dateTime: *, timeZone: string}, end: {dateTime: *, timeZone: string}, recurrence: Array, attendees: *[], reminders: {useDefault: boolean, overrides: *[]}}}
 */
var buildEvent = function(shift){
    console.log(shift);
    var event = {
        'summary':shift.psycho_name +  ' - משמרת ',
        'location': '',
        'description':shift.psycho_name +  ' - משמרת ',
        'start': {
            'dateTime': shift.start,
            'timeZone': 'Israel',
        },
        'end': {
            'dateTime': shift.end,
            'timeZone': 'Israel',
        },
        'recurrence': [
            //'RRULE:FREQ=DAILY;COUNT=2'
        ],
        'attendees': [
            {'email': shift.psycho_mail},
            {'email': 'togther.app@google.com'},
        ],
        'reminders': {
            'useDefault': false,
            'overrides': [
                {'method': 'email', 'minutes': 24 * 60},
                {'method': 'popup', 'minutes': 10},
            ],
        },
    };
    return event;
}



/**
 * Create an OAuth2 client with the given credentials, and then execute the
 * given callback function.
 *
 * @param {Object} credentials The authorization client credentials.
 * @param {function} callback The callback to call with the authorized client.
 */
function authorize(credentials, callback,addon,next) {
    var clientSecret = credentials.web.client_secret;
    var clientId = credentials.web.client_id;
    var redirectUrl = credentials.web.redirect_uris[0];
    var auth = new googleAuth();
    var oauth2Client = new auth.OAuth2(clientId, clientSecret, redirectUrl);

    // Check if we have previously stored a token.
    fs.readFile(TOKEN_PATH, function(err, token) {
        if (err) {
            getNewToken(oauth2Client, callback,addon,next);
        } else {
            oauth2Client.credentials = JSON.parse(token);
            callback(oauth2Client,addon,function(err,data){
                next(err,data);
            });
        }
    });
}

/**
 * Get and store new token after prompting for user authorization, and then
 * execute the given callback with the authorized OAuth2 client.
 *
 * @param {google.auth.OAuth2} oauth2Client The OAuth2 client to get token for.
 * @param {getEventsCallback} callback The callback to call with the authorized
 *     client.
 */
function getNewToken(oauth2Client, callback,addon,next) {
    var authUrl = oauth2Client.generateAuthUrl({
        access_type: 'offline',
        scope: SCOPES
    });
    console.log('Authorize this app by visiting this url: ', authUrl);
    var rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout
    });
    rl.question('Enter the code from that page here: ', function(code) {
        rl.close();
        oauth2Client.getToken(code, function(err, token) {
            if (err) {
                console.log('Error while trying to retrieve access token', err);
                next(err,null);
            }
            oauth2Client.credentials = token;
            storeToken(token);
            callback(oauth2Client,addon, next);
        });
    });
}

/**
 * Store token to disk be used in later program executions.
 *
 * @param {Object} token The token to store to disk.
 */
function storeToken(token) {
    try {
        fs.mkdirSync(TOKEN_DIR);
    } catch (err) {
        if (err.code != 'EEXIST') {
            throw err;
        }
    }
    fs.writeFile(TOKEN_PATH, JSON.stringify(token));
    console.log('Token stored to ' + TOKEN_PATH);
}

/**
 * Lists the next 10 events on the user's primary calendar.
 *
 * @param {google.auth.OAuth2} auth An authorized OAuth2 client.
 */
function listEvents(auth,max,next) {
    auth2 = auth;
    var calendar = google.calendar('v3');
    calendar.events.list({
        auth: auth,
        calendarId: 'primary',
        timeMin: (new Date()).toISOString(),
        maxResults: max,
        singleEvents: true,
        orderBy: 'startTime'
    }, function(err, response) {
        if (err) {
            console.log('The API returned an error: ' + err);
            next(err);
        }
        var events = response.items;
        if (events.length == 0) {
            next(err,'No upcoming events found.')
        } else {
            console.log('Upcoming 10 events:');
            var result = '';
            for (var i = 0; i < events.length; i++) {
                var event = events[i];
                var start = event.start.dateTime || event.start.date;
                result += "\n" + start + ":  " + event.summary;
            }
            next(err,result);
        }
    });
}

/**
 * Pushing the event to google calendar.
 * @param auth - authenticate object.
 * @param event - of shift we built.
 * @param next - calllback function.
 */
var pushEvent = function(auth,event,next){
    google.calendar('v3').events.insert({
        auth: auth,
        calendarId: 'primary',
        resource: event,
    }, function(err, event) {
        if (err) {
            console.log('There was an error contacting the Calendar service: ' + err);
            next(err);
        }
        if(event!=undefined){
            console.log('Event created: %s', event.htmlLink);
            next(err,'Event created: ' +  event.htmlLink)
        } else {
            next(err,null);
        }
    });

}

