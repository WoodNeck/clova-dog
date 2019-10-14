package com.cidteamq.clovaproject.dashboard

class LearningInfo internal constructor(
                                        internal var action: Int,
                                        internal var image: Int,
                                        internal var date: String,
                                        internal var command: String,
                                        internal var model: String,
                                        internal var voice: ArrayList<String>,
                                        internal var morpheme: ArrayList<String>,
                                        internal var result: FloatArray,
                                        internal var resultNext: FloatArray,
                                        internal var feedback: Int)
