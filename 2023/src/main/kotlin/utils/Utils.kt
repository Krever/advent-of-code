package org.example.day1.utils

object Utils {
    fun loadResource(name: String) = this.javaClass.getResource(name)!!.readText()
}