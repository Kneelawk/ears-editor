package com.kneelawk.earseditor.client

import com.kneelawk.earseditor.Log
import com.kneelawk.earseditor.mixin.impl.PlayerSkinTextureAccessor
import com.mojang.blaze3d.platform.TextureUtil
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.texture.NativeImage
import com.unascribed.ears.common.WritableEarsImage
import net.minecraft.client.texture.PlayerSkinTexture
import net.minecraft.client.util.DefaultSkinHelper
import net.minecraft.resource.ResourceManager
import java.io.ByteArrayInputStream
import java.util.*

class NativeImageBackedEarsPlayerSkinTexture(image: NativeImage?, private val playerUUID: UUID) : PlayerSkinTexture(
    null, "http://skins.minecraft.net/MinecraftSkins/magical-mahou.png", DefaultSkinHelper.getTexture(playerUUID),
    false, null
), WritableEarsImage {

    var image: NativeImage? = image
        set(value) {
            field?.close()
            field = value
        }

    init {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall {
                upload()
            }
        } else {
            upload()
        }
    }

    override fun load(manager: ResourceManager?) {
        // Don't do any actual loading.
    }

    fun upload() {
        val image = image
        if (image != null) {
            // apply ears effects (alpha decoding, features updating, and alpha stripping)
            val bais = ByteArrayInputStream(image.bytes)
            val newImage = (this as PlayerSkinTextureAccessor).callLoadTexture(bais)

            TextureUtil.prepareImage(getGlId(), newImage.width, newImage.height)
            bindTexture()
            newImage.upload(0, 0, 0, false)
        } else {
            Log.log.warn("Trying to upload disposed texture {}", getGlId())
        }
    }

    override fun close() {
        val image = image
        if (image != null) {
            image.close()
            clearGlId()
            this.image = null
        }
    }

    // copied from Ears
    private fun swap(i: Int): Int {
        return i and -0x1000000 or (i shl 16 and 0xFF0000) or (i and 0xFF00) or (i shr 16 and 0xFF)
    }

    override fun getWidth(): Int = image?.width ?: 64

    override fun getHeight(): Int = image?.height ?: 64

    override fun getARGB(x: Int, y: Int): Int {
        return image?.let { swap(it.getPixelColor(x, y)) } ?: 0
    }

    override fun setARGB(x: Int, y: Int, argb: Int) {
        image?.setPixelColor(x, y, swap(argb))
    }

    override fun copy(): WritableEarsImage {
        return NativeImageBackedEarsPlayerSkinTexture(image, playerUUID)
    }
}
