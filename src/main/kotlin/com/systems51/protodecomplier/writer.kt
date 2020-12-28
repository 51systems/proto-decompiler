package com.systems51.protodecomplier

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.Descriptors
import java.io.BufferedWriter
import java.io.File

fun decompile(descriptorPath: String, outputDir: String) {
    File(descriptorPath).inputStream().use {
        DescriptorProtos.FileDescriptorSet.parseFrom(it).writeProto(outputDir)
    }
}

fun DescriptorProtos.FileDescriptorSet.writeProto(outputDir: String) {
    fileList.forEach { it.writeProto(outputDir) }
}

fun DescriptorProtos.FileDescriptorProto.writeProto(outputDir: String) {
    val file = File("${outputDir}/${name}")

    println("Generating ${file.path}")

    file.parentFile.mkdirs()

    file.bufferedWriter().use { writer ->
        writer.apply {
            write("""syntax = "$syntax";""")
            newLine()

            write("package ${`package`};")
            newLine()

            options.allFields.forEach { (description, value) ->
                write("option ")
                write(description.name)
                write(" = ")

                if (description.type == Descriptors.FieldDescriptor.Type.STRING) {
                    write(""""$value"""")
                } else {
                    write(value.toString())
                }
                write(";")
                newLine()
            }

            dependencyList.forEach { dependency ->
                write("""import "$dependency";""")
                newLine()
            }

            newLine()

            messageTypeList.writeProto(writer)
            enumTypeList.writeProto(writer)

            serviceList.forEach { service ->
                write("service ${service.name} {")
                newLine()

                service.methodList.forEach { method ->
                    write("\t")
                    write("rpc ${method.name}(")

                    if (method.clientStreaming) {
                        write("stream ")
                    }

                    write("${method.inputType})")
                    write(" returns (")

                    if (method.serverStreaming) {
                        write("stream ")
                    }

                    write("${method.outputType});")
                    newLine()
                }

                write("}")
                newLine()
            }

            writer.flush()
        }
    }
}


@JvmName("writeDescriptorProtoList")
fun Collection<DescriptorProtos.DescriptorProto>.writeProto(writer: BufferedWriter) {
    forEach { it.writeProto(writer) }
}

fun DescriptorProtos.DescriptorProto.writeProto(writer: BufferedWriter) = writer.apply {
    write("message $name {")
    newLine()

    nestedTypeList.writeProto(writer)
    enumTypeList.writeProto(writer)
    fieldList.writeProto(writer)

    write("}")
    newLine()
    newLine()
}

@JvmName("writeFieldDescriptorProtoList")
fun Collection<DescriptorProtos.FieldDescriptorProto>.writeProto(writer: BufferedWriter) {
    sortedBy { it.number }.forEach { it.writeProto(writer) }
}

fun DescriptorProtos.FieldDescriptorProto.writeProto(writer: BufferedWriter) = writer.apply {
    write("\t")
    write(
        when (type) {
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE -> "double"
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_FLOAT -> "float"
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT64 -> "int64"
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT64 -> "uint64"
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32 -> "int32"
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED64 -> TODO()
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED32 -> TODO()
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL -> "bool"
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING -> "string"
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_GROUP -> TODO()
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE -> typeName
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_BYTES -> "bytes"
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT32 -> TODO()
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_ENUM -> typeName
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED32 -> TODO()
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED64 -> TODO()
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT32 -> TODO()
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT64 -> TODO()
        }
    )
    write(" $name = $number;")
    newLine()
}

@JvmName("writeEnumDescriptorProtoList")
fun Collection<DescriptorProtos.EnumDescriptorProto>.writeProto(writer: BufferedWriter) {
    forEach { it.writeProto(writer) }
}

fun DescriptorProtos.EnumDescriptorProto.writeProto(writer: BufferedWriter) = writer.apply {
    write("enum $name {")
    newLine()

    valueList.writeProto(writer)

    write("}")
    newLine()
    newLine()
}

fun Collection<DescriptorProtos.EnumValueDescriptorProto>.writeProto(writer: BufferedWriter) {
    sortedBy { it.number }.forEach { it.writeProto(writer) }
}

fun DescriptorProtos.EnumValueDescriptorProto.writeProto(writer: BufferedWriter) = writer.apply {
    write("\t")
    write("$name = $number;")
    newLine()
}
