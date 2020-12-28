package com.systems51.protodecomplier
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

fun main(args: Array<String>) = Decompile().main(args)

class Decompile: CliktCommand() {
    private val outputDir : String by option("--out", help="Output directory for generated protos").required()
    private val descriptorPath : String by argument(name="PATH", help="Path to descriptor file")

    override fun run() {
        decompile(descriptorPath, outputDir)
    }
}
