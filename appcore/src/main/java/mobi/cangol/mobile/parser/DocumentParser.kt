/**
 * Copyright (c) 2013 Cangol
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.parser

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.InputStream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

object DocumentParser {

    @Throws(XMLParserException::class)
    @JvmStatic
    fun parserDom(inputStream: InputStream): Element {
        val factory = DocumentBuilderFactory.newInstance()
        val builder: DocumentBuilder
        try {
            builder = factory.newDocumentBuilder()
            val document = builder.parse(inputStream)
            return document.documentElement
        } catch (e: Exception) {
            throw XMLParserException(e)
        }
    }

    @JvmStatic
    fun getNodeAttr(node: Node, attrName: String?): String {
        val element = node as Element
        return element.getAttribute(attrName)
    }

    @JvmStatic
    fun getNodeValue(parent: Node?, vararg nodeName: String?): String? {
        val nodeList = (parent as Element).getElementsByTagName(nodeName[0])
        if (null == nodeList) {
            return null
        } else {
            return if (nodeList.length > 0) {
                val node = nodeList.item(0)
                if (nodeName.size == 1) {
                    node.textContent
                } else {
                    val nodeNs = arrayOfNulls<String>(nodeName.size - 1)
                    for (i in 1 until nodeName.size) {
                        nodeNs[i - 1] = nodeName[i]
                    }
                    getNodeValue(node, *nodeNs)
                }
            } else {
                null
            }
        }
    }


    @JvmStatic
    fun getNodeList(parent: Node, nodeName: String?): NodeList {
        return (parent as Element).getElementsByTagName(nodeName)
    }

    @JvmStatic
    fun getNode(parent: Node, nodeName: String?): Node? {
        val nodeList = (parent as Element).getElementsByTagName(nodeName)
        return nodeList?.item(0)
    }
}
