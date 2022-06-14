/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import net.minecraft.network.chat.TranslatableComponent;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.Restriction;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.Objects;
import java.util.stream.Collectors;

public class MavenVersionStringHelper {
    public static String artifactVersionToString(final ArtifactVersion artifactVersion) {
        return artifactVersion.toString();
    }

    public static String versionRangeToString(final VersionRange range) {
        return range.getRestrictions().stream().map(MavenVersionStringHelper::restrictionToString).collect(Collectors.joining(", "));
    }

    public static String restrictionToString(final Restriction restriction) {
        if ( restriction.getLowerBound() == null && restriction.getUpperBound() == null )
        {
            return parseMessage("fml.messages.version.restriction.any");
        }
        else if ( restriction.getLowerBound() != null && restriction.getUpperBound() != null )
        {
            if (Objects.equals(artifactVersionToString(restriction.getLowerBound()), artifactVersionToString(restriction.getUpperBound())))
            {
                return artifactVersionToString(restriction.getLowerBound());
            }
            else
            {
                if (restriction.isLowerBoundInclusive() && restriction.isUpperBoundInclusive())
                {
                    return parseMessage("fml.messages.version.restriction.bounded.inclusive", restriction.getLowerBound(), restriction.getUpperBound());
                }
                else if (restriction.isLowerBoundInclusive())
                {
                    return parseMessage("fml.messages.version.restriction.bounded.upperexclusive", restriction.getLowerBound(), restriction.getUpperBound());
                }
                else if (restriction.isUpperBoundInclusive())
                {
                    return parseMessage("fml.messages.version.restriction.bounded.lowerexclusive", restriction.getLowerBound(), restriction.getUpperBound());
                }
                else
                {
                    return parseMessage("fml.messages.version.restriction.bounded.exclusive", restriction.getLowerBound(), restriction.getUpperBound());
                }
            }
        }
        else if ( restriction.getLowerBound() != null )
        {
            if ( restriction.isLowerBoundInclusive() )
            {
                return parseMessage("fml.messages.version.restriction.lower.inclusive", restriction.getLowerBound());
            }
            else
            {
                return parseMessage("fml.messages.version.restriction.lower.exclusive", restriction.getLowerBound());
            }
        }
        else
        {
            if ( restriction.isUpperBoundInclusive() )
            {
                return parseMessage("fml.messages.version.restriction.upper.inclusive", restriction.getUpperBound());
            }
            else
            {
                return parseMessage("fml.messages.version.restriction.upper.exclusive", restriction.getUpperBound());
            }
        }
    }

    //TODO better way of translating this
    static String parseMessage(String key, Object... args){
        return new TranslatableComponent(key, args).getContents();
    }

    public static void parseVersionRange(final StringBuffer stringBuffer, final Object range) {
        stringBuffer.append(versionRangeToString((VersionRange) range));
    }
}
