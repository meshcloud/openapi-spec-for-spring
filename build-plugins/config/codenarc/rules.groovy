// Every CodeNarc rule set, so that a new rule is on by default.
ruleset {
  ruleset('rulesets/basic.xml')
  ruleset('rulesets/braces.xml')
  ruleset('rulesets/comments.xml') {
    // A class gets a comment only for what its name and code cannot say.
    exclude('ClassJavadoc')
  }
  ruleset('rulesets/concurrency.xml') {
    // Misfires on gradle's DSL: it reads every property assignment in a configure block (`group = 'x'`) as an
    // untyped script variable.
    exclude('NoScriptBindings')
  }
  ruleset('rulesets/convention.xml') {
    // A precompiled script plugin cannot be compiled statically.
    CompileStatic { doNotApplyToFilesMatching = /.*\.gradle/ }
    // `def` for a local whose type the right-hand side states.
    exclude('NoDef')
    exclude('VariableTypeRequired')
    // `it` in a one-parameter closure, as in gradle's own DSL.
    exclude('ImplicitClosureParameter')
    // A class reads top-down: a non-public helper follows the method that first calls it.
    exclude('PublicMethodsBeforeNonPublicMethods')
    exclude('StaticMethodsBeforeInstanceMethods')
  }
  ruleset('rulesets/design.xml') {
    // Gradle instantiates tasks, build services and extensions through a generated subclass, so these classes are
    // abstract even when every method has a body.
    exclude('AbstractClassWithoutAbstractMethod')
  }
  ruleset('rulesets/dry.xml') {
    // Task groups and file names repeat by nature; a constant for each would hide the value it names.
    exclude('DuplicateStringLiteral')
  }
  ruleset('rulesets/enhanced.xml')
  ruleset('rulesets/exceptions.xml')
  ruleset('rulesets/formatting.xml') {
    Indentation { spacesPerIndentLevel = 2 }
    SpaceAroundMapEntryColon { characterAfterColonRegex = /\s/ }
    ClassEndsWithBlankLine { blankLineRequired = false }
    // A link is not wrapped.
    LineLength { ignoreLineRegex = /.*https?:\/\/.*/ }
  }
  ruleset('rulesets/generic.xml')
  ruleset('rulesets/grails.xml')
  ruleset('rulesets/groovyism.xml')
  ruleset('rulesets/imports.xml') {
    MisorderedStaticImports { comesBefore = false }
  }
  ruleset('rulesets/jdbc.xml')
  ruleset('rulesets/jenkins.xml')
  ruleset('rulesets/junit.xml')
  ruleset('rulesets/logging.xml')
  ruleset('rulesets/naming.xml')
  ruleset('rulesets/security.xml') {
    // Build logic works on files, and java.io.File is gradle's own API for them.
    exclude('JavaIoPackageAccess')
  }
  ruleset('rulesets/serialization.xml')
  ruleset('rulesets/size.xml') {
    // A precompiled script plugin counts as one method, so these measure the whole script.
    AbcMetric { doNotApplyToFilesMatching = /.*\.gradle/ }
    CyclomaticComplexity { doNotApplyToFilesMatching = /.*\.gradle/ }
    NestedBlockDepth { doNotApplyToFilesMatching = /.*\.gradle/ }
  }
  ruleset('rulesets/unnecessary.xml') {
    // Contradicts ImplicitReturnStatement; we keep the explicit return of Java.
    exclude('UnnecessaryReturnKeyword')
    // `map.empty` reads the key "empty", not isEmpty(), and `file.file` says less than `file.isFile()`.
    UnnecessaryGetter { checkIsMethods = false }
    // Asks for with() blocks, which hide the receiver in gradle's DSL (`tasks.register` after `tasks.register`).
    exclude('UnnecessaryObjectReferences')
  }
  ruleset('rulesets/unused.xml')
}
