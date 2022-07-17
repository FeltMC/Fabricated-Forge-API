package net.minecraftforge.client.extensions;

import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import io.github.fabricators_of_create.porting_lib.extensions.Matrix4fExtensions;

public interface IForgeMatrix4f extends Matrix4fExtensions {
    default void multiplyBackward(Matrix4f other) {
    }

    default void setTranslation(float x, float y, float z) {
    }

    default Matrix4f setMValues(float[] values){
        return new Matrix4f();
    }
}
