class Logger(var isDebug: Boolean = false) {

    fun debug(message: String){
        if(isDebug){
            println("[DEBUG] $message")
        }
    }

    fun info(message: String){
        println("[INFO] $message")
    }
}