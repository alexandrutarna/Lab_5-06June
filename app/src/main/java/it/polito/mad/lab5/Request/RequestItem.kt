package it.polito.mad.lab5.Request

import android.graphics.Bitmap

class RequestItem (username: String?,
                   dateLoan: String?,
                   dateReturn: String?,
                   msg: String?,
                   title: String?,
                   requestID: String?,
                   chatID: String?,
                   userImage: Bitmap?,
                   bookImage: Bitmap?,
                   status: String?) {

    var username : String? = null
    var dateLoan : String? = null
    var dateReturn : String? = null
    var msg : String? = null
    var title : String? = null
    var requestID : String? = null
    var chatID: String? = null
    var userImage : Bitmap? = null
    var bookImage : Bitmap? = null
    var status : String? = null
    var loanerID : String? = null
    var ownerID : String? = null

    init {
        this.username = username
        this.dateLoan = dateLoan
        this.dateReturn = dateReturn
        this.msg = msg
        this.title = title
        this.requestID = requestID
        this.chatID = chatID
        this.userImage = userImage
        this.bookImage = bookImage
        this.status = status
    }

    constructor(username: String?,
                dateLoan: String?,
                dateReturn: String?,
                msg: String?,
                title: String?,
                requestID: String?,
                chatID: String?,
                userImage: Bitmap?,
                bookImage: Bitmap?,
                status: String?,
                loanerID : String?,
                ownerID : String?) :
            this(username, dateLoan, dateReturn, msg, title, requestID, chatID, userImage, bookImage, status) {

        this.loanerID = loanerID
        this.ownerID = ownerID

    }

}