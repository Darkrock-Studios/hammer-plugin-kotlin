import hammer.ActionDriver
import hammer.ActionRequest
import hammer.FakeHammerHost
import hammer.host
import hammer.ops.SceneKind
import hammer.ops.SceneNode
import hammer.ops.SceneText
import hammer.ops.SceneTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

class WordCountTest {
	private val text = mapOf(2 to "Rain fell hard.", 3 to "Quiet.")

	// A stand-in for Hammer, answering the operations the plugin calls with the kit's own types.
	private val fake = FakeHammerHost(
		mapOf(
			"scene.tree" to { _ ->
				Json.encodeToJsonElement(
					SceneTree(
						listOf(
							SceneNode(1, "Part One", SceneKind.Group, children = listOf(SceneNode(2, "Storm"), SceneNode(3, "Calm"))),
						)
					)
				)
			},
			"scene.read" to { input -> Json.encodeToJsonElement(SceneText(markdown = text.getValue(input["id"]!!.jsonPrimitive.int))) },
		),
	).also { host = it }

	private fun driver(place: String = "project", id: Int? = null) =
		ActionDriver("count", ::countWords, "Storm", context = ActionRequest.Context(place, id))

	@Test
	fun `counts every scene, with a total`() {
		assertEquals("# Word count\n\n- Storm: 3\n- Calm: 1\n\n**Total: 4**\n", driver().open().run().page?.markdown)
		assertEquals(listOf("Counting Storm", "Counting Calm"), fake.progress.map { it["message"]!!.jsonPrimitive.content })
	}

	@Test
	fun `counts the scenes picked, or the scene it was run from`() {
		assertEquals("# Word count\n\n- Storm: 3\n", driver().open().fill("scenes", listOf(2)).run().page?.markdown)
		assertEquals("# Word count\n\n- Calm: 1\n", driver("scene", 3).open().run().page?.markdown)
	}

	@Test
	fun `words are runs of letters and digits`() {
		assertEquals(4, wordsIn("She said — twice — “no.”"))
	}
}
