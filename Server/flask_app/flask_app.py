from flask import Flask, jsonify, request, abort, Response
from flask.ext.httpauth import HTTPTokenAuth
from mongokit import *

MONGODB_HOST = 'localhost'
MONGODB_PORT = 27017
DB = 'mc'
USERS_COLLECTION = 'users'
MEMO_COLLECTION = 'memos'

app = Flask(__name__)
app.config.from_object(__name__)
conn = Connection(app.config['MONGODB_HOST'], app.config['MONGODB_PORT'])
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


@auth.verify_token
def verify_token(token):
    try:
        print token
        db_token = conn.User.one({"token" : token})
        print db_token
        return True if db_token else False
    except Exception as e:
        print e.message
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


@app.route("/resource/memo_details", strict_slashes=False)
@auth.login_required
def memo_details():
    # TODO: provide access to memo to user

    return Response("Success!")


if __name__ == '__main__':
    app.run()
