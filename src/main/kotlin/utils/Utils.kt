package utils

object Utils {
    fun loadResource(name: String) = this.javaClass.getResource(name)!!.readText()
}