package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

import gov.va.api.health.r4.api.resources.Resource;
import java.util.stream.Stream;

/**
 * To add witness protection for your resource type, create an implementation of this interface that
 * is available in the Spring context.
 *
 * <pre>
 * &#64;Component
 * public class WhateverWitnessProtectionAgent implements WitnessProtectionAgent&lt;Whatever> {
 *   Stream&lt;ProtectedReference> referencesOf(Whatever whatever) {
 *     // do whatever you need here
 *   }
 * }
 * </pre>
 */
public interface WitnessProtectionAgent<ResourceT extends Resource> {
  /** Determine non-null, potentially empty references of the given instance. */
  Stream<ProtectedReference> referencesOf(ResourceT resource);
}
