import hammer.ActionReply
import hammer.ActionRequest
import hammer.input
import hammer.ops.Hammer
import hammer.ops.SceneKind
import hammer.ops.SceneNode
import hammer.progress
import kotlinx.serialization.Serializable

/** The action's first page: the manifest's `[[actions.field]]`. */
@Serializable
class CountInput(val scenes: List<Int> = emptyList())

/**
 * Counts the words in the scenes picked, or the scene it was run from, or else the whole story, and replies
 * with a document the writer can copy or save as a note.
 */
fun countWords(request: ActionRequest): ActionReply {
	val here = request.context.id.takeIf { request.context.place == "scene" }
	val chosen = request.input<CountInput>().scenes.ifEmpty { listOfNotNull(here) }
	val scenes = Hammer.scene.tree(request.project).nodes.flatMap { it.scenes() }
		.filter { chosen.isEmpty() || it.id in chosen }
	if (scenes.isEmpty()) return ActionReply.message("There are no scenes to count")

	val counts = scenes.mapIndexed { i, scene ->
		progress(i.toDouble() / scenes.size, "Counting ${scene.name}")
		scene.name to wordsIn(Hammer.scene.read(request.project, scene.id).markdown)
	}
	return ActionReply.document(
		buildString {
			appendLine("# Word count")
			appendLine()
			counts.forEach { (name, words) -> appendLine("- $name: $words") }
			if (counts.size > 1) appendLine("\n**Total: ${counts.sumOf { it.second }}**")
		},
		title = "Word count",
	)
}

/** This node's scenes in story order, groups opened up. */
private fun SceneNode.scenes(): List<SceneNode> = if (kind == SceneKind.Scene) listOf(this) else children.flatMap { it.scenes() }

/** Words are runs that hold at least one letter or digit, so dashes and stray punctuation don't count. */
fun wordsIn(text: String): Int = text.split(' ', '\n', '\t').count { it.any(Char::isLetterOrDigit) }
