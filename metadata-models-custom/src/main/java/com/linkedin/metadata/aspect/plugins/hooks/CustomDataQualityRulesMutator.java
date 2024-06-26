package com.linkedin.metadata.aspect.plugins.hooks;

import com.linkedin.metadata.aspect.AspectRetriever;
import com.linkedin.metadata.aspect.batch.ChangeMCP;
import com.linkedin.metadata.aspect.plugins.config.AspectPluginConfig;
import com.linkedin.util.Pair;
import com.mycompany.dq.DataQualityRule;
import com.mycompany.dq.DataQualityRules;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class CustomDataQualityRulesMutator extends MutationHook {

  public CustomDataQualityRulesMutator(AspectPluginConfig config) {
    super(config);
  }

  @Override
  protected Stream<Pair<ChangeMCP, Boolean>> writeMutation(
      @Nonnull Collection<ChangeMCP> changeMCPS, @Nonnull AspectRetriever aspectRetriever) {
    return changeMCPS.stream()
        .map(
            changeMCP -> {
              boolean mutated = false;

              if (changeMCP.getRecordTemplate() != null) {
                DataQualityRules newDataQualityRules =
                    new DataQualityRules(changeMCP.getRecordTemplate().data());

                for (DataQualityRule rule : newDataQualityRules.getRules()) {
                  // Ensure uniform lowercase
                  if (!rule.getType().toLowerCase().equals(rule.getType())) {
                    mutated = true;
                    rule.setType(rule.getType().toLowerCase());
                  }
                }
              }

              return mutated ? changeMCP : null;
            })
        .filter(Objects::nonNull)
        .map(changeMCP -> Pair.of(changeMCP, true));
  }
}
