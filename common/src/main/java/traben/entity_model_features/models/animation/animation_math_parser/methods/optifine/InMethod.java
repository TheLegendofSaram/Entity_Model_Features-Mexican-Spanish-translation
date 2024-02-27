package traben.entity_model_features.models.animation.animation_math_parser.methods.optifine;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.animation_math_parser.MathComponent;
import traben.entity_model_features.models.animation.animation_math_parser.MathMethod;

import java.util.ArrayList;
import java.util.List;

public class InMethod extends MathMethod {


    public InMethod(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

        var parsedArgs = parseAllArgs(args, calculationInstance);

        MathComponent x = parsedArgs.get(0);
        List<MathComponent> vals = new ArrayList<>(parsedArgs);
        vals.remove(0);

        setSupplierAndOptimize(() -> {
            float X = x.getResult();
            for (MathComponent expression :
                    vals) {
                if (expression.getResult() == X) {
                    return 1f;
                }
            }
            return 0f;
        }, parsedArgs);
    }



    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount >= 2;
    }

}
