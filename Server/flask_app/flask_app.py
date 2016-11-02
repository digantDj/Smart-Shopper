from flask import Flask, jsonify, request, abort, Response
from flask.ext.httpauth import HTTPTokenAuth
from mongokit import *
import os

#MONGODB_HOST = os.environ['OPENSHIFT_MONGODB_DB_HOST'] 
#MONGODB_PORT = int(os.environ['OPENSHIFT_MONGODB_DB_PORT'])
# If you're running mongodb on your localhost, comment above 2 lines and use
# the below two.
# MONGODB_HOST = 'localhost'
# MONGODB_PORT = 27017
MONGODB_URL = os.environ['OPENSHIFT_MONGODB_DB_URL']
DB = 'mc'
USERS_COLLECTION = 'users'
MEMO_COLLECTION = 'memos'
GROUP_MEMO_COLLECTION = 'groupmemos'

app = Flask(__name__)
app.config.from_object(__name__)
#conn = Connection(app.config['MONGODB_HOST'], app.config['MONGODB_PORT'])
conn = Connection(app.config['MONGODB_URL'])
auth = HTTPTokenAuth(scheme='Token')

@conn.register
class User(Document):
    __database__ = DB
    __collection__ = USERS_COLLECTION

    structure = {
        "username" : unicode,
        "email" : unicode,
        "token" : unicode
    }


@conn.register
class Memo(Document):
    __database__ = DB
    __collection__ = MEMO_COLLECTION
    structure = {
        "username" : unicode,
        "memo": {
            "memo_id" : unicode,
            "detail" : unicode
        }
    }
	
@conn.register
class GroupMemo(Document):
    __database__ = DB
    __collection__ = GROUP_MEMO_COLLECTION
    structure = {
        "userid" : unicode,
		"groupid" : unicode,
		"category" : unicode,
		"content" : unicode,
		"status" : unicode,
		"type" : unicode

    }
	


@auth.verify_token
def verify_token(token):
    try:
        app.logger.debug("token :" + token)
        db_token = conn.User.one({"token" : token})
        app.logger.debug("db_token : " + db_token)
        return True if db_token else False
    except Exception as e:
        app.logger.error(e.message)
        return False


@app.route('/', strict_slashes=False)
def root():
    return jsonify({
        "heartbeat" : "success"
    })


@app.route("/register", methods=["POST"], strict_slashes=False)
def register():
    request_incoming_json = request.get_json()
    if not request_incoming_json:
        return abort(400)
    try:
        user = conn.User()
        user["username"] = request_incoming_json.get("username")
        user["email"] = request_incoming_json.get("email")
        user["token"] = request_incoming_json.get("token")
        user.save()
        return jsonify({"message": "Success"})

    except Exception as e:
        print e.message
        return Response("Error occured while registering user.")
		
@app.route("/create_memo", methods=["POST"], strict_slashes=False)
def create_memo():
    request_incoming_json = request.get_json()
    if not request_incoming_json:
        return abort(400)
    try:
        memo = conn.GroupMemo()
        memo["userid"] = request_incoming_json.get("userid")
        memo["groupid"] = request_incoming_json.get("groupid")
        memo["category"] = request_incoming_json.get("category")
        memo["content"] = request_incoming_json.get("content")
        memo["status"] = request_incoming_json.get("status")
        memo["type"] = request_incoming_json.get("type")
        memo.save()
        return jsonify({"message": "Success"})

    except Exception as e:
        print e.message
        return Response("Error occured while creating memo.")		


@app.route("/resource/memo_details", strict_slashes=False)
@auth.login_required
def memo_details():
    # TODO: provide access to memo to user

    return Response("Success!")


if __name__ == '__main__':
    app.run()
