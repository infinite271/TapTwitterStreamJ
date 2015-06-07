var stompClient = null;
var uuid = guid();

function setConnected(connected) {
    //document.getElementById('connect').disabled = connected;
    //document.getElementById('disconnect').disabled = !connected;
}

function connect() {
    var socket = new SockJS('/getTweets');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/tweets/' + uuid, function (tweet) {
            processTweet(JSON.parse(tweet.body).content);
        });
        stompClient.subscribe('/topic/hashtags/' + uuid, function (hashtags) {
            processHashTags(JSON.parse(hashtags.body));
        });
        stompClient.subscribe('/topic/keywordStatistics/' + uuid, function (hashtags) {
            processKeywordStatistics(JSON.parse(hashtags.body));
        });
        document.getElementById("connectButton").className = "button success disabled";
        document.getElementById("disconnectButton").className = "button alert";
        document.getElementById("sendFilterParameters").className = "button success postfix";
        document.getElementById("fieldsetLegend").innerHTML = "Connection Status - Connected";
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.send("/app/shutdown", {}, JSON.stringify({'sessionId': uuid}));
        stompClient.disconnect();
    }
    setConnected(false);
    document.getElementById("fieldsetLegend").innerHTML = "Connection Status - Disconnected";
    document.getElementById("connectButton").className = "button success";
    document.getElementById("disconnectButton").className = "button alert disabled";
    document.getElementById("sendFilterParameters").className = "button success disabled postfix";
    console.log("Disconnected");
}

function guid() {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }

    return s4() + s4() + s4() + s4() + s4() + s4() + s4() + s4();
}

function sendFilterParameters() {
    var filterParameters = document.getElementById('filterParameters').value;
    stompClient.send("/app/getTweets", {}, JSON.stringify({
        'sessionId': uuid,
        'filterParameters': filterParameters
    }));
}

function processTweet(message) {
    var str = JSON.stringify(message, null, 3);
    var tableRef = document.getElementById('tweetsTable').getElementsByTagName('tbody')[0];

    // Insert a row in the table at the last row
    var newRow = tableRef.insertRow(tableRef.rows.length);

    // Insert a cell in the row at index 0
    var newCell = newRow.insertCell(0);

    // Append a text node to the cell
    var newText = document.createTextNode(str);
    newCell.appendChild(newText);

}

function processKeywordStatistics(message) {
    var tableRows = document.getElementById("keywordStatisticsTable").rows.length;

    if (tableRows >= 1) {
        var table = document.getElementById("keywordStatisticsTable");
        for (var i = table.rows.length - 1; i > 0; i--) {
            table.deleteRow(i);
        }
    }

    var str = JSON.stringify(message, null, 3);
    var tableRef = document.getElementById('keywordStatisticsTable').getElementsByTagName('tbody')[0];

    // Insert a row in the table at the last row
    var newRow = tableRef.insertRow(tableRef.rows.length);

    // Insert a cell in the row at index 0
    var newCell = newRow.insertCell(0);

    // Append a text node to the cell
    var newText = document.createTextNode(str);
    newCell.appendChild(newText);
}

function processHashTags(message) {
    var tableRows = document.getElementById("hashtagsTable").rows.length;

    if (tableRows >= 1) {
        var table = document.getElementById("hashtagsTable");
        for (var i = table.rows.length - 1; i > 0; i--) {
            table.deleteRow(i);
        }
    }

    var str = JSON.stringify(message, null, 3);
    var tableRef = document.getElementById('hashtagsTable').getElementsByTagName('tbody')[0];

    // Insert a row in the table at the last row
    var newRow = tableRef.insertRow(tableRef.rows.length);

    // Insert a cell in the row at index 0
    var newCell = newRow.insertCell(0);

    // Append a text node to the cell
    var newText = document.createTextNode(str);
    newCell.appendChild(newText);
}

window.onbeforeunload = function(){
    disconnect()
};