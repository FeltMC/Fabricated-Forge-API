package net.fabricatedforgeapi.asm.modeldata;

import org.spongepowered.asm.mixin.injection.selectors.ElementNode;
import org.spongepowered.asm.mixin.injection.selectors.ISelectorContext;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelector;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelectorByName;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelectorDynamic;
import org.spongepowered.asm.mixin.injection.selectors.InvalidSelectorException;
import org.spongepowered.asm.mixin.injection.selectors.MatchResult;

@ITargetSelectorDynamic.SelectorId("RedirectHandler")
public class RedirectHandlerSelector implements ITargetSelectorDynamic, ITargetSelectorByName {
    private final String name;

    public RedirectHandlerSelector(String name) {
        this.name = name;
    }

    @Override
    public String getOwner() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDesc() {
        return null;
    }

    @Override
    public String toDescriptor() {
        return "";
    }

    @Override
    public MatchResult matches(String owner, String name, String desc) {
        return name.startsWith("redirect$") && name.endsWith("$" + this.name) ? MatchResult.EXACT_MATCH : MatchResult.NONE;
    }

    @Override
    public ITargetSelector next() {
        return null;
    }

    @Override
    public ITargetSelector configure(Configure request, String... args) {
        return this;
    }

    @Override
    public ITargetSelector validate() throws InvalidSelectorException {
        return this;
    }

    @Override
    public ITargetSelector attach(ISelectorContext context) throws InvalidSelectorException {
        return this;
    }

    @Override
    public int getMinMatchCount() {
        return 0;
    }

    @Override
    public int getMaxMatchCount() {
        return 1;
    }

    @Override
    public <TNode> MatchResult match(ElementNode<TNode> node) {
        return this.matches(node.getOwner(), node.getName(), node.getDesc());
    }

    public static RedirectHandlerSelector parse(String input, ISelectorContext context) {
        return new RedirectHandlerSelector(input);
    }
}
