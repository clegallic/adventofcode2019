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

func compute(intCodeInt []int) int {
	w := make([]int, len(intCodeInt))
	copy(w, intCodeInt)
	for i := 0; i < len(w); i += 4 {
		opCode := w[i]
		if opCode == 1 {
			w[w[i+3]] = w[w[i+1]] + w[w[i+2]]
		} else if opCode == 2 {
			w[w[i+3]] = w[w[i+1]] * w[w[i+2]]
		} else if opCode == 99 {
			break
		}
	}
	return w[0]
}

func main() {
	input, _ := ioutil.ReadFile("../input.txt")
	targetOutput := 19690720

	intCodeStr := strings.Split(strings.Split(string(input), "\n")[0], ",")
	var intCodeInt = make([]int, len(intCodeStr))
	for i := 0; i < len(intCodeInt); i++ {
		intCodeInt[i] = toInt(intCodeStr[i])
	}

	for noun := 0; noun < len(intCodeInt); noun++ {
		intCodeInt[1] = noun
		for verb := 0; verb < len(intCodeInt); verb++ {
			intCodeInt[2] = verb
			if compute(intCodeInt) == targetOutput {
				fmt.Printf("Noun = %v, Verb = %v, Result = %v\n", noun, verb, 100*noun+verb)
			}
		}
	}
}
