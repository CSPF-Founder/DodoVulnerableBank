__author__ = 'CSPF'
from ..config import app
from .. model.Transaction import Transaction
from .. model.Account import Account
from flask import request,jsonify
@app.route("/transaction/statement/<int:customer_id>",methods=['POST',"GET"])
def view_statement(customer_id):
    transactions = Transaction.query.filter_by(customer_id=customer_id).all()
    if not transactions:
        return "", 204
    else:
        statement = {}
        for transaction in transactions:
            statement.update({transaction.id:
                                  {"amount":transaction.amount,"remarks":transaction.remarks,
                                  "details":transaction.transaction_details,"sender_account_number":transaction.sender_account_number,
                                  "receiver_account_number":transaction.receiver_account_number
                                   }
                             })
        return jsonify(statement)

@app.route("/transaction/transfer",methods=['POST',"GET"])
def transfer_amount():
    customer_id = request.form['customer_id']
    to_account = request.form['to_account']
    amount = request.form['amount']
    account = Account.query.filter_by(id=customer_id).first()
    if account is None:
        return jsonify({"success":False})
    else:
        account.debit(amount)
        receiver_account= Account.query.filter_by(account_number=to_account).first()
        if receiver_account:
            receiver_account.credit(amount)

        transaction = Transaction(account.id,amount,account.account_number,to_account,
                                  remarks="Transferred",
                                  transaction_details="Transferred from "+account.account_number+" to "+to_account)
        transaction.insert()
        return jsonify({"success":True})