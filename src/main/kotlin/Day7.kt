fun main() {
    val root = processInput(getInput())

    println(root.dirs().filter { it.totalSize <= 100_000 }.sumOf { it.totalSize })
    val needToClear = root.totalSize - 40_000_000
    println(root.dirs().map { it.totalSize }.filter { it >= needToClear }.min())
}

private fun processInput(input: String): Directory {
    val cwd = mutableListOf<String>()
    val root = Directory("/", mutableMapOf())
    for (line in input.lines().drop(1)) {
        if (line == "$ ls") {
            continue
        } else if (line.startsWith("$ cd")) {
            val dir = line.substring(5)
            if (dir == "..") {
                cwd.removeLast()
            } else {
                cwd.add(dir)
            }
        } else if (line.startsWith("dir")) {
            val name = line.split(' ', limit = 2)[1]
            (root.navigate(cwd) as Directory).files[name] = Directory(name, mutableMapOf())
        } else {
            val parts = line.split(' ', limit = 2)
            val size = parts[0].toLong()
            val name = parts[1]
            (root.navigate(cwd) as Directory).files[name] = RegularFile(name, size)
        }
    }
    return root
}

private sealed interface File
private class Directory(val name: String, val files: MutableMap<String, File>): File {
    fun navigate(path: List<String>): File {
        return path.fold(this as File) { cwd, name -> (cwd as Directory).files[name] ?: error(path) }
    }

    val totalSize by lazy { totalSize() }

    private fun totalSize(): Long {
        return files.values.sumOf {
            when (it) {
                is Directory -> it.totalSize()
                is RegularFile -> it.size
            }
        }
    }

    fun dirs(): Sequence<Directory> {
        return sequence {
            yield(this@Directory)
            for (file in files.values) {
                if (file is Directory) {
                    yieldAll(file.dirs())
                }
            }
        }
    }

    override fun toString(): String {
        return "Directory(name=$name)"
    }
}
private data class RegularFile(val name: String, val size: Long): File
