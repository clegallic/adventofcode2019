package main

import (
	"fmt"
	"io/ioutil"
	"strconv"
	"strings"
)

func toInt(s string) int {
	i, _ := strconv.Atoi(s)
	return i
}

func compute(intCodeInt []int) {
	for i := 0; i <= len(intCodeInt); i += 4 {
		opCode := intCodeInt[i]
		if opCode == 1 {
			intCodeInt[intCodeInt[i+3]] = intCodeInt[intCodeInt[i+1]] + intCodeInt[intCodeInt[i+2]]
		} else if opCode == 2 {
			intCodeInt[intCodeInt[i+3]] = intCodeInt[intCodeInt[i+1]] * intCodeInt[intCodeInt[i+2]]
		} else if opCode == 99 {
			break
		}
	}
	fmt.Println(intCodeInt)
}

func main() {
	input, _ := ioutil.ReadFile("../input.txt")
	intCodeStr := strings.Split(strings.Split(string(input), "\n")[0], ",")
	var intCodeInt = make([]int, len(intCodeStr))
	for i := 0; i < len(intCodeInt); i++ {
		intCodeInt[i] = toInt(intCodeStr[i])
	}
	intCodeInt[1] = 12
	intCodeInt[2] = 2
	compute(intCodeInt)
}
