package traben.entity_model_features.models.animation.animation_math_parser;

import traben.entity_model_features.utils.EMFUtils;

public class MathConstant extends MathValue implements MathComponent {


    public static MathConstant ZERO = new MathConstant(0) {
        @Override
        public void makeNegative(boolean become) {
        }

        //make this constant to avoid object instantiation
        private static final ResultSupplier supplier = () -> 0;
        @Override
        ResultSupplier getResultSupplier() {
            return supplier;
        }
    };
    public static MathConstant ONE = new MathConstant(1) {
        @Override
        public void makeNegative(boolean become) {
        }
        //make this constant to avoid object instantiation
        private static final ResultSupplier supplier = () -> 1;
        @Override
        ResultSupplier getResultSupplier() {
            return supplier;
        }
    };

    private float hardCodedValue;

    public MathConstant(float number, boolean isNegative) {
        hardCodedValue = isNegative ? -number : number;
    }

    public MathConstant(float number) {
        hardCodedValue = number;
    }

    @Override
    ResultSupplier getResultSupplier() {
        EMFUtils.logError("EMF math constant: this shouldn't happen!");
        return () -> hardCodedValue;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public void makeNegative(boolean become) {
        if (become) hardCodedValue = -hardCodedValue;
    }

    @Override
    public String toString() {
        return String.valueOf(getResult());
    }

    @Override // make faster return
    public float getResult() {
        return hardCodedValue;
    }
}
