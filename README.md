# fingerprint-auth

val intent = Intent(this, FingerprintAuthenticationActivity::class.java)<br/>
ActivityCompat.startActivityForResult(this, intent, Constants.REQUEST_CODE, null)

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.hasExtra(Constants.AUTH_STATUS) && data.hasExtra(Constants.AUTH_MESSAGE)) {
                    if (data.getBooleanExtra(Constants.AUTH_STATUS, false)) {
                        textViewAuthenticate.setText("Success")
                        textViewAuthenticate.setBackgroundColor(this.resources.getColor(R.color.colorPrimaryGrey))
                    } else {
                        textViewAuthenticate.setText("Failed")
                        textViewAuthenticate.setBackgroundColor(this.resources.getColor(R.color.error_color_material))
                    }
                }
            }
        }
    }
