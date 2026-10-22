import hammer.action
import kotlin.wasm.WasmExport

// Hammer calls this for every action in the manifest; tell them apart by name.
@WasmExport
fun action(): Int = action { request ->
	when (request.action) {
		"count" -> countWords(request)
		else -> null
	}
}
