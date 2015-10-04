__author__ = 'CSPF'
__author__ = 'CSPF'
from ..config import app
from .. model.Account import Account
from flask import request,jsonify

@app.route("/account/beneficiaries/<int:customer_id>",methods=['POST',"GET"])
def view_beneficiaries(customer_id):
    beneficiaries = Account.query.filter(id != customer_id).all()
    if beneficiaries is None:
        return "", 204
    else:
        beneficiaries_list = []
        for beneficiary in beneficiaries:
            if beneficiary.id != customer_id:
                beneficiaries_list.append(beneficiary.account_number)
        return jsonify({"account_numbers":beneficiaries_list})

@app.route("/account/details/<int:customer_id>",methods=['POST',"GET"])
def view_account_details(customer_id):
    details = Account.query.filter_by(id = customer_id).first()
    if details is None:
        return " ", 204
    else:
        return jsonify({"account_number":details.account_number,"balance":details.balance})