// Playground - noun: a place where people can play

import Cocoa

var str = "Hello, playground"

println(str)

let explicitFloat: Float = 4

let label = "The width is "
let width = 94
let widthLabel = "\(label) \(width)"

var optName : String?

optName = "John Appleseed"

var greeting : String
if let name = optName {
    greeting = "Hello \(name)"
} else {
    greeting = "Hello!"
}

println(greeting)

let vegetable : String? = "red pepper"

var comment : String
switch vegetable {
case let x where x?.hasPrefix("red"):
    comment = "hot"
case let x where x?.hasSuffix("pepper"):
    comment = "spicy"
default:
    comment = "usual"
}

let interesstingNumbers = [
    "Prime": [2, 3, 5, 7, 11, 13],
    "Fibonacci": [1, 1, 2, 3, 5, 8],
    "Square": [1, 4, 9, 16, 25]
]

var largest = 0
var largestKind = ""
for (kind, numbers) in interesstingNumbers {
    for number in numbers {
        if number > largest {
            largest = number
            largestKind = kind
        }
    }
}
let largestest = "Largest Number \(largest) of Kind \(largestKind)"

var firstForLoop = 0
for i in 0..<4 {
    firstForLoop += i
}
firstForLoop

func greet(name:String, day:String) -> String {
    return "Hello \(name), today is \(day)"
}
greet("Bob", "Tuesday")

func getGasPrices() -> (Double, Double, Double) {
    return (3.59, 3.69, 3.79)
}
getGasPrices()

func sumOf(numbers:Int...) -> Int {
    var sum = 0
    for number in numbers {
        sum += number
    }
    return sum
}
sumOf()
sumOf(42, 597, 12)

func avgOf(numbers:Int...) -> Int {
    var sum = 0
    for number in numbers {
        sum += number
    }
    return sum / numbers.count
}
avgOf(23, 32, 42, 64)

var numbers = [20, 19, 7, 12]

numbers.map(
    {(number:Int) -> Int in
        return 3 * number
    }
)

