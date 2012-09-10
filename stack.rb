class Stack

  attr_accessor :stack
  def initialize
    @stack = Array.new
  end

  def push(item)
    @stack.push item
  end

  def pop
    @stack.pop
  end

  def count
    @stack.length
  end

  def clear
    @stack.clear
  end

  def empty?
    @stack.empty?
  end
end