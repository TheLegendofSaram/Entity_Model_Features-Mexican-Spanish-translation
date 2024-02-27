package traben.entity_model_features.models.animation.animation_math_parser;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.animation_math_parser.methods.MethodRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class MathMethod extends MathValue implements MathComponent {


    private MathComponent optimizedAlternativeToThis = null;
    private ResultSupplier supplier = null;

    protected MathMethod(boolean isNegative, EMFAnimation calculationInstance, int argCount) throws EMFMathException {
        super(isNegative, calculationInstance);
        if (!hasCorrectArgCount(argCount)) {
            throw new EMFMathException("ERROR: wrong number of arguments [" + argCount + "] in [" + this.getClass().getSimpleName() + "] for [" + calculationInstance.animKey + "] in [" + calculationInstance.modelName + "].");
        }
    }

    protected static MathComponent parseArg(String arg, EMFAnimation calculationInstance) throws EMFMathException {
        if (arg == null || arg.isBlank())
            throw new EMFMathException("Method argument parsing error [" + arg + "] in [" + calculationInstance.animKey + "] in [" + calculationInstance.modelName + "].");
        var ret = MathExpressionParser.getOptimizedExpression(arg, false, calculationInstance);

        if (ret == MathExpressionParser.NULL_EXPRESSION) {
            throw new EMFMathException("Method argument parsing null [" + arg + "] in [" + calculationInstance.animKey + "] in [" + calculationInstance.modelName + "].");
        }
        return ret;
    }

    protected static List<MathComponent> parseAllArgs(List<String> args, EMFAnimation calculationInstance) throws EMFMathException {
        if (args == null || args.isEmpty())
            throw new EMFMathException("Method argument parsing error [" + args + "] in [" + calculationInstance.animKey + "] in [" + calculationInstance.modelName + "].");
        List<MathComponent> expressionList = new ArrayList<>();
        for (String arg : args) {
            expressionList.add(parseArg(arg, calculationInstance));
        }
        return expressionList;
    }

    private static MathMethod of(String methodNameIn, String args, boolean isNegative, EMFAnimation calculationInstance) throws EMFMathException {

        String methodName;
        boolean booleanInvert = methodNameIn.startsWith("!");
        if (booleanInvert) {
            methodName = methodNameIn.replaceFirst("!", "");
        } else {
            methodName = methodNameIn;
        }

        //first lets split the args into a list
        List<String> argsList = new ArrayList<>();

        int openBracketCount = 0;
        StringBuilder builder = new StringBuilder();
        for (char ch :
                args.toCharArray()) {
            switch (ch) {
                case '(' -> {
                    openBracketCount++;
                    builder.append(ch);
                }
                case ')' -> {
                    openBracketCount--;
                    builder.append(ch);
                }
                case ',' -> {
                    if (openBracketCount == 0) {
                        argsList.add(builder.toString());
                        builder = new StringBuilder();
                    } else {
                        builder.append(ch);
                    }
                }
                default -> builder.append(ch);
            }
        }
        if (!builder.isEmpty()) {
            argsList.add(builder.toString());
        }

        if (!MethodRegistry.getInstance().containsMethod(methodName)) {
            throw new EMFMathException("ERROR: Unknown method [" + methodName + "], rejecting animation expression for [" + calculationInstance.animKey + "].");
        }

        MathMethod method = MethodRegistry.getInstance().getMethodFactory(methodName).getMethod(argsList, isNegative, calculationInstance);

        if (booleanInvert) {
            method.invertSupplierBoolean();
        }
        return method;
    }

    static MathComponent getOptimizedExpression(String methodName, String args, boolean isNegative, EMFAnimation calculationInstance) throws EMFMathException {
        MathMethod method = of(methodName, args, isNegative, calculationInstance);
        return Objects.requireNonNullElse(method.optimizedAlternativeToThis, method);
    }

    protected void setOptimizedAlternativeToThis(final MathComponent optimizedAlternativeToThis) {
        this.optimizedAlternativeToThis = optimizedAlternativeToThis;
    }

    protected boolean canOptimizeForConstantArgs() {
        return true;
    }

    protected void setSupplierAndOptimize(ResultSupplier supplier) {
        this.supplier = supplier;
        setOptimizedIfPossible(supplier, List.of());
    }

    protected void setSupplierAndOptimize(ResultSupplier supplier, MathComponent arg) {
        this.supplier = supplier;
        setOptimizedIfPossible(supplier, List.of(arg));
    }

    protected void setSupplierAndOptimize(ResultSupplier supplier, List<MathComponent> allArgs) {
        this.supplier = supplier;
        setOptimizedIfPossible(supplier, allArgs);
    }

    protected abstract boolean hasCorrectArgCount(int argCount);

    private void invertSupplierBoolean() {
        if (optimizedAlternativeToThis == null) {
            supplier = () -> supplier.get() == 1 ? 0 : 1;
        } else {
            optimizedAlternativeToThis = new MathConstant(optimizedAlternativeToThis.getResult() == 1 ? 0 : 1, isNegative);
        }
    }

    protected void setOptimizedIfPossible(ResultSupplier supplier, List<MathComponent> allComponents) {
        //check if method only contains constants, if so precalculate the result and replace this with a constant
        if (!canOptimizeForConstantArgs()) return;

        boolean foundNonConstant = false;
        for (MathComponent comp :
                allComponents) {
            if (!comp.isConstant()) {
                foundNonConstant = true;
                break;
            }
        }
        if (!foundNonConstant) {
            //precalculate expression that only contains constants
            float constantResult = supplier.get();
            if (!Float.isNaN(constantResult))
                optimizedAlternativeToThis = new MathConstant(constantResult, isNegative);
        }
    }


    @Override
    ResultSupplier getResultSupplier() {
        return supplier;
    }
}
