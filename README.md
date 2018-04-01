# fingerprint-auth


<b>Add this code to initiate the authentication</b>
val intent = Intent(this, FingerprintAuthenticationActivity::class.java)
ActivityCompat.startActivityForResult(this, intent, Constants.REQUEST_CODE, null)

<b>Add this code to get the result</b>
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == Constants.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
        if (data != null) {
            if (data.hasExtra(Constants.AUTH_STATUS) && data.hasExtra(Constants.AUTH_MESSAGE)) {
                if (data.getBooleanExtra(Constants.AUTH_STATUS, false)) {
                  //Success
                } else {
                  //Failed
                }
            }
        }
    }
}
