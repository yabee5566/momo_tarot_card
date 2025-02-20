package com.onean.momo.data.network.exception

import java.io.IOException

class ServerResponseError(val code: Int, override val message: String) : IOException()
