import scalariform.formatter.preferences._
import ScalariformKeys._
scalariformSettings

preferences := preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)