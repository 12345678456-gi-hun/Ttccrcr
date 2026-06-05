package com.example.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object McpackExporter {
    private const val TAG = "McpackExporter"

    fun exportAndShare(context: Context, workspaceName: String, files: List<ProjectFile>): File? {
        val sanitizedName = workspaceName.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            .ifBlank { "void_pack" }
        val exportFile = File(context.cacheDir, "$sanitizedName.mcpack")
        
        try {
            if (exportFile.exists()) {
                exportFile.delete()
            }
            exportFile.createNewFile()

            ZipOutputStream(FileOutputStream(exportFile)).use { zos ->
                for (projFile in files) {
                    val entry = ZipEntry(projFile.path)
                    zos.putNextEntry(entry)
                    if (projFile.isBinary && projFile.binaryData != null) {
                        zos.write(projFile.binaryData)
                    } else {
                        zos.write(projFile.content.toByteArray(Charsets.UTF_8))
                    }
                    zos.closeEntry()
                }
            }
            
            Log.d(TAG, "Mcpack successfully zipped to File: ${exportFile.absolutePath} (Size: ${exportFile.length()} bytes)")

            // Build share intent using FileProvider
            val authority = "${context.packageName}.fileprovider"
            val uri: Uri = FileProvider.getUriForFile(context, authority, exportFile)
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/octet-stream"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Minecraft Add-on: $workspaceName")
                putExtra(Intent.EXTRA_TEXT, "Exported Minecraft Bedrock Add-on package (.mcpack) from V.O.I.D. IDE!")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            val chooserIntent = Intent.createChooser(intent, "Export .mcpack to...").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooserIntent)
            
            return exportFile
        } catch (e: IOException) {
            Log.e(TAG, "Failed to zip or share .mcpack package", e)
            return null
        }
    }
}
